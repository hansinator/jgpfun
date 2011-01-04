package jgpfun.gui;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.plaf.BorderUIResource;
import layout.SpringUtilities;

/**
 *
 * @author Hansinator
 */
public class InfoPanel extends JPanel {

    private final JLabel labelRPS;

    private final JLabel labelProgress;

    private final JLabel labelGeneration;


    public void updateInfo(Integer rps, Integer progress, Integer generation) {
        labelRPS.setText(rps.toString());
        labelProgress.setText(progress.toString());
        labelGeneration.setText(generation.toString());
    }


    public InfoPanel() {
        JLabel labelRPSText = new JLabel("RPS:");
        JLabel labelProgessText = new JLabel("Progress:");
        JLabel labelGenerationText = new JLabel("Generation:");
        labelRPS = new JLabel();
        labelProgress = new JLabel();
        labelGeneration = new JLabel();

        labelRPSText.setLabelFor(labelRPS);
        labelProgessText.setLabelFor(labelProgress);

        setLayout(new SpringLayout());
        setPreferredSize(new Dimension(200, 1));
        setMinimumSize(new Dimension(200, 1));
        setBorder(BorderUIResource.getEtchedBorderUIResource());
        setAlignmentY(TOP_ALIGNMENT);
        setAlignmentX(LEFT_ALIGNMENT);

        add(labelRPSText);
        add(labelRPS);
        add(labelProgessText);
        add(labelProgress);
        add(labelGenerationText);
        add(labelGeneration);

        SpringUtilities.makeCompactGrid(this,
                                3, 2,        //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad
    }

}
