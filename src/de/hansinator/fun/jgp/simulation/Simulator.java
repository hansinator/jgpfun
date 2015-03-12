package de.hansinator.fun.jgp.simulation;

import java.io.File;
import java.util.Random;

import org.jfree.data.xy.XYSeries;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.UserAbort;

import de.hansinator.fun.jgp.genetics.GenealogyTree;
import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.Genome.GenomeEvaluator;
import de.hansinator.fun.jgp.gui.MainFrame;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.util.Settings;

public class Simulator
{

	static
	{
		Settings.load(new File("default.properties"));
	}

	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	private final WorldSimulation simulation;

	protected Genome[] currentGeneration;

	private final GenealogyTree genealogyTree;

	private final Scenario<Genome> scenario;

	private final Random rng;

	public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

	public final XYSeries fitnessChartData = new XYSeries("fitness");

	public final XYSeries genomeSizeChartData = new XYSeries("prg size");

	public final XYSeries realGenomeSizeChartData = new XYSeries("real prg size");

	private final int popSize = Settings.getInt("popSize");
	
	private final int eliteCount = Settings.getInt("eliteCount");
	
	private final UserAbort abort = new UserAbort();

	private EvolutionEngine<Genome> engine;

	public Simulator(Scenario<Genome> scenario)
	{
		this.scenario = scenario;
		simulation = scenario.getSimulation();
		currentGeneration = new Genome[popSize];
		genealogyTree = new GenealogyTree();
		rng = Settings.newRandomSource();

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
	synchronized public void start()
	{
		abort.reset();
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Simulator.this.run();
			}
		}, "SimulationThread" + this).start();
	}

	private void run()
	{
		long now, evaluationsPerMinuteAverage = 0, evaluationsPerMinuteCount = 0;
		long startTime = System.currentTimeMillis();
		long lastStatsTime = startTime;
		int lastEvaluationCount = 0;

		// setup simulation
		simulation.initialize();
		System.out.println("Start time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(startTime));
		
		// setup engine
        FitnessEvaluator<Genome> evaluator = new CachingFitnessEvaluator<Genome>(new GenomeEvaluator());
        engine = new GenerationalEvolutionEngine<Genome>(scenario.getCandidateFactory(), scenario.createEvolutionPipeline(), evaluator, scenario.getSelectionStrategy(), rng);
        engine.addEvolutionObserver(new ConsoleEvoLog());
        //engine.addEvolutionObserver(monitor);

		// run simulation
        engine.evolve(popSize, eliteCount, new TerminationCondition[]{ abort });

		// print statistics
		System.out.println("\nEnd time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));
		System.out.println("Runtime: "
				+ PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, System.currentTimeMillis())));
	}

	public void stop()
	{
		abort.abort();
		simulation.stop();
	}

	public void restart()
	{
		stop();
		reset();
		start();
	}

	private void reset()
	{
		engine = null;
		genealogyTree.clear();
		fitnessChartData.clear();
		genomeSizeChartData.clear();
		realGenomeSizeChartData.clear();
		statisticsHistory.clear();
	}

	private int calculateTotalFitness(Genome[] generation)
	{
		int totalFit = 0;
		for (Genome g : generation)
			totalFit += g.getFitnessEvaluator().getFitness();
		return totalFit;
	}

	/**
	 * temporary callback solution
	 */
	void printPopStats(int totalFood, int generation)
	{
		int avgProgSize = 0, avgRealProgSize = 0;

		for (Genome g : currentGeneration)
			avgProgSize += g.getRootGene().getSize();
		avgProgSize /= currentGeneration.length;

		for (Genome g : currentGeneration)
			avgRealProgSize += g.getRootGene().getExonSize();
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
	
	
    static class ConsoleEvoLog implements EvolutionObserver<Genome>
    {
    	@Override
        public void populationUpdate(PopulationData<? extends Genome> data)
        {
        	// update population statistics
			printPopStats(totalFitness, evaluationCount);
			fitnessChartData.add(evaluationCount, totalFitness);

			// statistics
			System.out.println("GEN: " + evaluationCount);
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
			
            System.out.printf("Generation %d: %s\n",
                              data.getGenerationNumber(),
                              data.getBestCandidate());
        }
    }
}
