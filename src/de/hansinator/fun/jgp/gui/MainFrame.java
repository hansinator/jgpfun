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

import de.hansinator.fun.jgp.genetics.Genome;
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

	private JPanel simulationClientView;

	public final JPanel sidePaneRight;
	
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
        //scenario.getEngine().engine.addEvolutionObserver(monitor);

		// create all sub views
		sidePaneRight = new StatisticsHistoryPanel(scenario.getEvoStats().statisticsHistory);
		simulationClientView = scenario.getSimulationView();
		simulationClientView.setPreferredSize(new Dimension(width, height));

		// add the menu bar
		setJMenuBar(createMenuBar());

		// setup and add all stuff to the content pane
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(simulationClientView, BorderLayout.CENTER);
		contentPane.add(sidePaneRight, BorderLayout.LINE_END);

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

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		new MainFrame(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), new FindingFoodScenario()).startSimulation();
	}
}
