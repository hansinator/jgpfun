package de.hansinator.fun.jgp.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;

import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.simulation.WorldEvolutionEngine;

/**
 * 
 * @author Hansinator
 */
public class ControlPanel extends JPanel
{

	public ControlPanel(final EvolutionaryProcess simulator)
	{
		JCheckBox speedSwitch = new JCheckBox("Fast mode", true);
		speedSwitch.addActionListener(new ActionListener()
		{

			boolean slowMode = false;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				slowMode = !slowMode;
				simulator.getSimulation().setSlowMode(slowMode);
			}

		});

		final JSlider speedSlider = new JSlider(0, WorldEvolutionEngine.ROUNDS_PER_GENERATION);
		speedSlider.setMajorTickSpacing(WorldEvolutionEngine.ROUNDS_PER_GENERATION / 2);
		speedSlider.setMinorTickSpacing(WorldEvolutionEngine.ROUNDS_PER_GENERATION / 8);
		speedSlider.setPaintLabels(true);
		speedSlider.setPaintTicks(true);
		speedSlider.setValue(simulator.getSimulation().getRoundsMod());
		speedSlider.setMaximumSize(new Dimension(200, 40));
		speedSlider.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				simulator.getSimulation().setRoundsMod(speedSlider.getValue());
			}

		});

		final JSlider fpsSlider = new JSlider(0, 100);
		fpsSlider.setMajorTickSpacing(50);
		fpsSlider.setMinorTickSpacing(25);
		fpsSlider.setPaintLabels(true);
		fpsSlider.setPaintTicks(true);
		fpsSlider.setValue(70);
		fpsSlider.setMaximumSize(new Dimension(200, 40));
		fpsSlider.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				simulator.getSimulation().setFps(fpsSlider.getValue());
			}

		});

		final JButton pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener()
		{

			boolean paused = false;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				paused = !paused;
				pauseButton.setText(paused ? "Resume" : "Pause");
				simulator.getSimulation().setPaused(paused);
			}

		});

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
		setPreferredSize(new Dimension(200, 0));
		// setMinimumSize(new Dimension(120, 0));
		setBorder(BorderUIResource.getEtchedBorderUIResource());

		add(speedSwitch);
		add(speedSlider);
		add(fpsSlider);
		add(pauseButton);
	}

}
