package jgpfun.gui;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.BorderUIResource;

/**
 *
 * @author Hansinator
 */
public class InfoPanel extends JPanel {
    private final JLabel labelRPS;
    private final JLabel labelProgress;

    public void updateInfo(Integer rps, Integer progress) {
        labelRPS.setText(rps.toString());
        labelProgress.setText(progress.toString());
    }

    public InfoPanel() {
        labelRPS = new JLabel();
        labelProgress = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Container.LEFT_ALIGNMENT);
        setAlignmentY(Container.TOP_ALIGNMENT);
        setPreferredSize(new Dimension(150, 200));
        setBorder(BorderUIResource.getEtchedBorderUIResource());

        add(labelRPS);
        add(labelProgress);
    }
}
