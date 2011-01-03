package jgpfun.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.BorderUIResource;

/**
 *
 * @author Hansinator
 */
public class ControlPanel extends JPanel {

    public ControlPanel(ActionListener speedListener) {
        JButton speedSwitch = new JButton("fast/slow");
        speedSwitch.addActionListener(speedListener);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentY(TOP_ALIGNMENT);
        setAlignmentX(LEFT_ALIGNMENT);
        setPreferredSize(new Dimension(120, 0));
        setMinimumSize(new Dimension(120, 0));
        setBorder(BorderUIResource.getEtchedBorderUIResource());

        add(speedSwitch);
    }
}
