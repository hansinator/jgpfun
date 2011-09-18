package jgpfun.gui;

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
import jgpfun.life.Simulation;

/**
 *
 * @author Hansinator
 */
public class ControlPanel extends JPanel {

    public ControlPanel(final Simulation simulation) {
        JCheckBox speedSwitch = new JCheckBox("Fast mode", true);
        speedSwitch.addActionListener(new ActionListener() {

            boolean slowMode = false;


            @Override
            public void actionPerformed(ActionEvent e) {
                slowMode = !slowMode;
                simulation.setSlowMode(slowMode);
            }

        });

        final JSlider speedSlider = new JSlider(1, 4000);
        speedSlider.setMaximumSize(new Dimension(200, 20));
        speedSlider.setValue(simulation.getRoundsMod());
        speedSlider.addChangeListener(new ChangeListener() {


            @Override
            public void stateChanged(ChangeEvent e) {
                simulation.setRoundsMod(speedSlider.getValue());
            }
        });

        final JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {

            boolean paused = false;


            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
                pauseButton.setText(paused ? "Resume" : "Pause");
                simulation.setPaused(paused);
            }

        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentY(TOP_ALIGNMENT);
        setAlignmentX(LEFT_ALIGNMENT);
        setPreferredSize(new Dimension(200, 0));
        //setMinimumSize(new Dimension(120, 0));
        setBorder(BorderUIResource.getEtchedBorderUIResource());

        add(speedSwitch);
        add(speedSlider);
        add(pauseButton);
    }

}
