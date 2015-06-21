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
import de.hansinator.fun.jgp.simulation.WorldSimulation;

/**
 * 
 * @author Hansinator
 */
public class ControlPanel extends JPanel
{

	public ControlPanel(final WorldSimulation simulator)
	{
		JCheckBox speedSwitch = new JCheckBox("Fast mode", true);
		speedSwitch.addActionListener(new ActionListener()
		{

			boolean slowMode = false;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				slowMode = !slowMode;
				simulator.setSlowMode(slowMode);
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
				simulator.setFps(fpsSlider.getValue());
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
				simulator.setPaused(paused);
			}

		});

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
		setPreferredSize(new Dimension(140, 0));
		setBorder(BorderUIResource.getEtchedBorderUIResource());

		add(speedSwitch);
		add(fpsSlider);
		add(pauseButton);
	}

}
