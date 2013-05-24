package de.hansinator.fun.jgp.simulation;

import java.io.File;
import java.util.Random;

import org.jfree.data.xy.XYSeries;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import de.hansinator.fun.jgp.genetics.GenealogyTree;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.gui.InfoPanel;
import de.hansinator.fun.jgp.gui.MainFrame;
import de.hansinator.fun.jgp.gui.MainView;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.life.OrganismGene;
import de.hansinator.fun.jgp.util.Settings;

public class Simulator
{

	static
	{
		Settings.load(new File("default.properties"));
	}

	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	/*
	 * The chance with which crossover happens, rest is mutation.
	 */
	public static final double crossoverRate = Settings.getDouble("crossoverRate");

	public static final int maxMutations = Settings.getInt("maxMutations");

	private final WorldSimulation simulation;

	protected OrganismGene[] currentGeneration;

	private final SelectionStrategy selector;

	private final CrossoverOperator crossover;

	private final GenealogyTree genealogyTree;

	private final Scenario scenario;

	private final Random rnd;

	public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

	public final XYSeries fitnessChartData = new XYSeries("fitness");

	public final XYSeries genomeSizeChartData = new XYSeries("prg size");

	public final XYSeries realGenomeSizeChartData = new XYSeries("real prg size");

	private final Object runLock = new Object();

	private final int popSize;

	private int evaluationCount;

	private volatile boolean running;

	public Simulator(Scenario scenario)
	{
		this.scenario = scenario;
		simulation = scenario.getSimulation();
		popSize = Settings.getInt("popSize");
		currentGeneration = new OrganismGene[popSize];
		genealogyTree = new GenealogyTree();
		selector = scenario.getSelectionStrategy();
		crossover = scenario.getCrossoverOperator();
		rnd = Settings.newRandomSource();

		fitnessChartData.setMaximumItemCount(500);
		genomeSizeChartData.setMaximumItemCount(500);
		realGenomeSizeChartData.setMaximumItemCount(500);
		reset();
	}

	/**
	 * Start a stopped simulation without resetting it.
	 * 
	 * TODO: do not start a new simulation when one is running
	 * 
	 * @param mainView
	 * @param infoPanel
	 */
	synchronized public void start(final MainView mainView, final InfoPanel infoPanel)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Simulator.this.run(mainView, infoPanel);
			}
		}, "SimulationThread" + this).start();
	}

	private void run(MainView mainView, InfoPanel infoPanel)
	{
		long now, evaluationsPerMinuteAverage = 0, evaluationsPerMinuteCount = 0;
		long startTime = System.currentTimeMillis();
		long lastStatsTime = startTime;
		int lastEvaluationCount = 0, totalFitness;

		// setup simulation
		simulation.initialize();
		System.out.println("Start time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(startTime));

		// run simulation
		running = true;
		synchronized (runLock)
		{
			while (running)
			{
				// evaluate organisms
				currentGeneration = simulation.evaluate(this, currentGeneration, mainView, infoPanel);
				totalFitness = calculateTotalFitness(currentGeneration);
				evaluationCount++;

				// update population statistics
				printPopStats(totalFitness, evaluationCount);
				fitnessChartData.add(evaluationCount, totalFitness);

				// produce new generation
				currentGeneration = newGeneration(currentGeneration, totalFitness);

				// statistics
				System.out.println("GEN: " + evaluationCount);
				infoPanel.updateInfo(evaluationCount + 1);
				now = System.currentTimeMillis();
				if ((now - lastStatsTime) >= 3000)
				{
					long evaluationsPerMinute = (evaluationCount - lastEvaluationCount)
							* (60000 / (now - lastStatsTime));
					evaluationsPerMinuteAverage += evaluationsPerMinute;
					evaluationsPerMinuteCount++;

					System.out.println("GPM: " + evaluationsPerMinute);
					System.out.println("Runtime: "
							+ PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, now)));
					lastEvaluationCount = evaluationCount;
					lastStatsTime = now;
				}
			}
		}

		// simulation statistics
		System.out.println("\nEnd time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));
		System.out.println("Runtime: "
				+ PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, System.currentTimeMillis())));
		System.out.println("Average GPM: "
				+ ((evaluationsPerMinuteCount > 0) ? (evaluationsPerMinuteAverage / evaluationsPerMinuteCount) : 0));
	}

	public void stop()
	{
		running = false;
		simulation.stop();
	}

	public void restart(MainView mainView, InfoPanel infoPanel)
	{
		simulation.stop();
		reset();
		start(mainView, infoPanel);
	}

	private void reset()
	{
		if (running)
			stop();

		// ensure that the main loop has ended before manipulating these
		synchronized (runLock)
		{
			genealogyTree.clear();
			fitnessChartData.clear();
			genomeSizeChartData.clear();
			realGenomeSizeChartData.clear();
			statisticsHistory.clear();
			evaluationCount = 0;

			for (int i = 0; i < popSize; i++)
			{
				OrganismGene g = scenario.randomGenome();
				currentGeneration[i] = g;
				genealogyTree.put(g);
			}
		}
	}

	private OrganismGene[] newGeneration(OrganismGene[] generation, int totalFitness)
	{
		OrganismGene child1, child2, parent1, parent2;
		OrganismGene[] newAnts = new OrganismGene[generation.length];

		// create new genomes via cloning and mutation or crossover
		for (int i = 0; i < (generation.length / 2); i++)
		{
			// select two source genomes and clone them
			parent1 = selector.select(generation, totalFitness);
			parent2 = selector.select(generation, totalFitness);
			child1 = parent1.replicate();
			child2 = parent2.replicate();

			// mutate or crossover with a user defined chance
			// if (rnd.nextDouble() > crossoverRate) {
			// mutate genomes
			child1.mutate(rnd.nextInt(maxMutations) + 1);
			child2.mutate(rnd.nextInt(maxMutations) + 1);
			/*
			 * } else { //perform crossover crossover.cross(child1.program,
			 * child2.program, rnd); }
			 */

			// create new ants from the modified genomes and save them
			newAnts[i*2] = child1;
			newAnts[i*2+1] = child2;

			// add to genealogy tree
			genealogyTree.put(parent1, child1);
			genealogyTree.put(parent2, child2);
		}

		return newAnts;
	}

	private int calculateTotalFitness(OrganismGene[] generation)
	{
		int totalFit = 0;
		for (OrganismGene g : generation)
			totalFit += g.getFitness();
		return totalFit;
	}

	/**
	 * temporary callback solution
	 */
	void printPopStats(int totalFood, int generation)
	{
		int avgProgSize = 0, avgRealProgSize = 0;

		for (OrganismGene g : currentGeneration)
			avgProgSize += g.size();
		avgProgSize /= currentGeneration.length;

		for (OrganismGene g : currentGeneration)
			avgRealProgSize += g.getExonSize();
		avgRealProgSize /= currentGeneration.length;

		statisticsHistory
		.appendEntry(generation, totalFood, totalFood / currentGeneration.length, avgProgSize, avgRealProgSize);
		genomeSizeChartData.add(generation, avgProgSize);
		realGenomeSizeChartData.add(generation, avgRealProgSize);
	}

	public WorldSimulation getSimulation()
	{
		return simulation;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		Simulator sim = new Simulator(new FindingFoodScenario());
		new MainFrame(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), sim).startSimulation();
	}
}
