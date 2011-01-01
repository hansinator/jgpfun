package jgpfun.gui;

import java.awt.Container;
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
        speedSwitch.setAlignmentX(Container.LEFT_ALIGNMENT);
        speedSwitch.setAlignmentY(Container.TOP_ALIGNMENT);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Container.LEFT_ALIGNMENT);
        setAlignmentY(Container.TOP_ALIGNMENT);
        setPreferredSize(new Dimension(150, 200));
        setBorder(BorderUIResource.getEtchedBorderUIResource());

        add(speedSwitch);
    }
}
