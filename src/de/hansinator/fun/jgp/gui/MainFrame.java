package de.hansinator.fun.jgp.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.jfree.data.xy.XYSeries;
import org.joda.time.format.PeriodFormat;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.simulation.FindingFoodScenario;
import de.hansinator.fun.jgp.simulation.Scenario;
import de.hansinator.fun.jgp.simulation.WorldSimulation;
import de.hansinator.fun.jgp.util.Settings;

/**
 * MainFrame is some sort of a view for a Simulator instance
 * @author hansinator
 */
public class MainFrame extends JFrame implements WindowListener
{
	static
	{
		Settings.load(new File("default.properties"));
	}

	private WorldSimulationView simulationClientView;

	public final JPanel sidePaneLeft, sidePaneRight;

	public final BottomPanel bottomPane;
	
	private final Scenario<Genome> scenario;
	
	private final WorldSimulation evaluationStrategy;

	public MainFrame(int width, int height, Scenario<Genome> scenario)
	{
		super("BAH! Bonn!!1!11!!!");
		this.scenario = scenario;
		
		// get EvaluationStrategy - this is our workhorse component for the moment.
		// most computation happens during evaluation for our scenario, in contrast
		// to the watchmaker examples where the evaluation is just a tiny fraction
		// of the computational load. we need to save a reference to the strategy
		// to pass it to the UI components for fine grained control over its
		// execution (such as pausing, slow/fast mode, etc). in watchmaker examples
		// it is sufficient to render the best candidate every once a while, because
		// they compute new candidates more than once a second. here instead we'll
		// need to render the evaluation process itself multiple times - it is
		// not sufficient to show just the solution, but the process itself
		// XXX because there is no API yet we need to cast this to a proper type
		// TODO in the future the scenario should create the views for us, b/c it knows about types
		evaluationStrategy = (WorldSimulation)scenario.getEvaluationStrategy();
		
		EvoStats evoStats = new EvoStats();
		scenario.getEngine().addEvolutionObserver(evoStats);
        //scenario.getEngine().engine.addEvolutionObserver(monitor);

		// create all sub views
		sidePaneLeft = new JPanel();
		sidePaneRight = new StatisticsHistoryPanel(evoStats.statisticsHistory);
		bottomPane = new BottomPanel(evaluationStrategy, evoStats);
		
		// init simulation client view
		simulationClientView = new WorldSimulationView(evaluationStrategy);
		simulationClientView.setPreferredSize(new Dimension(width, height));

		// add the menu bar
		setJMenuBar(createMenuBar());

		// setup and add all stuff to the content pane
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(simulationClientView, BorderLayout.CENTER);
		contentPane.add(sidePaneLeft, BorderLayout.LINE_START);
		contentPane.add(sidePaneRight, BorderLayout.LINE_END);
		contentPane.add(bottomPane, BorderLayout.PAGE_END);

		// get ready for action
		pack();
		setVisible(true);
		
		
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(new JMenu("File")).add(new JMenuItem("Exit")).addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				stopSimulation();
			}

		});

		JMenu simulationMenu = new JMenu("Simulation");
		simulationMenu.add(new JMenuItem("Reset"));
		simulationMenu.add(new JMenuItem("Preferences"));
		menuBar.add(simulationMenu);

		return menuBar;
	}

	public void startSimulation()
	{
		System.out.println("Simulation client view size: " + simulationClientView.getWidth() + "x" + simulationClientView.getHeight());
		
		// TODO: to be put somewhere else
		addWindowListener(this);
		
		// run simulator
		scenario.getEvolutionaryProcess().start();
	}

	public void stopSimulation()
	{
		evaluationStrategy.stop();
		scenario.getEvolutionaryProcess().stop();
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		stopSimulation();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		stopSimulation();
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	static class EvoStats implements EvolutionObserver<Genome>
    {
    	public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

    	public final XYSeries fitnessChartData = new XYSeries("fitness");

    	public final XYSeries genomeSizeChartData = new XYSeries("prg size");

    	public final XYSeries realGenomeSizeChartData = new XYSeries("real prg size");
    	
    	private int lastEvaluationCount = 0, generation = 0;
    	
    	long lastStatsTime, evaluationsPerMinuteAverage = 0, evaluationsPerMinuteCount = 0;
    	
    	public EvoStats()
		{
    		fitnessChartData.setMaximumItemCount(500);
    		genomeSizeChartData.setMaximumItemCount(500);
    		realGenomeSizeChartData.setMaximumItemCount(500);
    		//XXX this is a crude solution
    		lastStatsTime = System.currentTimeMillis();
		}

    	public void reset()
    	{
    		fitnessChartData.clear();
    		genomeSizeChartData.clear();
    		realGenomeSizeChartData.clear();
    		statisticsHistory.clear();
    		//XXX this is a crude solution
    		lastStatsTime = System.currentTimeMillis();
    	}
    	
    	@Override
        public void populationUpdate(PopulationData<? extends Genome> data)
        {
    		generation = data.getGenerationNumber();

    		// population statistics
    		{
	    		int avgProgSize = 0, avgRealProgSize = 0, totalFit = 0;
	    		int popSize = data.getPopulationSize();
	
	    		for (EvaluatedCandidate<? extends Genome> e : data.getEvaluatedPopulation())
	    		{
	    			Genome g = e.getCandidate();
	    			avgProgSize += g.getRootGene().getSize();
	    			avgRealProgSize += g.getRootGene().getExonSize();
	    			totalFit += g.getFitnessEvaluator().getFitness();
	    		}
	    		avgProgSize /= popSize;
	    		avgRealProgSize /= popSize;
	    		
	    		
	    		statisticsHistory.appendEntry(generation, data.getBestCandidateFitness(), data.getMeanFitness(), avgProgSize, avgRealProgSize);
	    		genomeSizeChartData.add(generation, avgProgSize);
	    		realGenomeSizeChartData.add(generation, avgRealProgSize);
				fitnessChartData.add(generation, data.getMeanFitness());
				System.out.println("GEN: " + generation);
    		}

    		// performance statistics
    		{
    			long now;
    			
				now = System.currentTimeMillis();
				if ((now - lastStatsTime) >= 3000)
				{
					long evaluationsPerMinute = (generation - lastEvaluationCount)
							* (60000 / (now - lastStatsTime));
					evaluationsPerMinuteAverage += evaluationsPerMinute;
					evaluationsPerMinuteCount++;
	
					System.out.println("GPM: " + evaluationsPerMinute);
					System.out.println("Runtime: " + PeriodFormat.getDefault().print(new org.joda.time.Period(data.getElapsedTime())));
					lastEvaluationCount = generation;
					lastStatsTime = now;
				}
    		}
        }
    	
    	public int getGenerationNumber()
    	{
    		return generation;
    	}
    }
	
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		new MainFrame(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), new FindingFoodScenario()).startSimulation();
	}
}
