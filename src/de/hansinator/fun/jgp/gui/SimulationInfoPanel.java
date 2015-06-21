package de.hansinator.fun.jgp.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.plaf.BorderUIResource;

import util.SpringUtilities;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.simulation.WorldSimulation;
import de.hansinator.fun.jgp.simulation.WorldSimulation.SimulationViewUpdateListener;

/**
 * 
 * @author Hansinator
 */
public class SimulationInfoPanel extends JPanel
{
	private final WorldSimulation simulator;
	
	private final JLabel labelRPS;

	private final JLabel labelProgress;

	private final JLabel labelGeneration;
	
	private final EvoStats evoStats;

	
	public SimulationInfoPanel(final WorldSimulation simulator, EvoStats evoStats)
	{
		this.simulator = simulator;
		this.evoStats = evoStats;
		
		simulator.addViewUpdateListener( new SimulationViewUpdateListener() {
			
			@Override
			public void onViewUpdate()
			{
				updateInfo();
			}
		});
		
		JLabel labelRPSText = new JLabel("RPS:");
		JLabel labelProgessText = new JLabel("Progress:");
		JLabel labelGenerationText = new JLabel("Generation:");
		labelRPS = new JLabel();
		labelProgress = new JLabel();
		labelGeneration = new JLabel();

		labelRPSText.setLabelFor(labelRPS);
		labelProgessText.setLabelFor(labelProgress);

		setLayout(new SpringLayout());
		setMinimumSize(new Dimension(140, 1));
		setBorder(BorderUIResource.getEtchedBorderUIResource());
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);

		add(labelRPSText);
		add(labelRPS);
		add(labelProgessText);
		add(labelProgress);
		add(labelGenerationText);
		add(labelGeneration);

		SpringUtilities.makeCompactGrid(this, 3, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
		
		// make this fixed width
		((SpringLayout)getLayout()).getConstraints(this).setConstraint(SpringLayout.WIDTH, Spring.constant(140));
	}

	private void updateInfo()
	{
		labelRPS.setText("" + simulator.getRPS());
		labelProgress.setText("" + (simulator.getCurrentRound() * 100) / WorldSimulation.ROUNDS_PER_GENERATION);
		labelGeneration.setText("" + (evoStats.getGenerationNumber() + 1));
	}
}
