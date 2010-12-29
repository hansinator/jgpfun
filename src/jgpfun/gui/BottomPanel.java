package jgpfun.gui;

import java.awt.Container;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author hansinator
 */
public class BottomPanel extends JPanel {

    public BottomPanel(ActionListener speedListener) {
        JButton speedSwitch = new JButton("fast/slow");
        speedSwitch.addActionListener(speedListener);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentY(Container.LEFT_ALIGNMENT);
        add(speedSwitch);
    }

}
