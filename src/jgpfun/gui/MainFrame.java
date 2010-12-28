package jgpfun.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jgpfun.Main.UpdatableListModel;

/**
 *
 * @author hansinator
 */
public class MainFrame extends JFrame {

    public final MainView mainView;

    private final JList foodList;

    public void updateFoodList() {
        ((UpdatableListModel) foodList.getModel()).update();
    }

    public MainFrame(int width, int height, ActionListener speedListener, UpdatableListModel foodHist) {
        super("BAH! Bonn!!1!11!!!");

        mainView = new MainView();
        mainView.setPreferredSize(new Dimension(width, height));

        foodList = new JList(foodHist);
        foodList.setPreferredSize(new Dimension(200, 0));
        foodList.setAlignmentY(Container.TOP_ALIGNMENT);

        JButton speedSwitch = new JButton("fast/slow");
        speedSwitch.addActionListener(speedListener);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setAlignmentY(Container.TOP_ALIGNMENT);
        controlPanel.add(speedSwitch);
        controlPanel.add(foodList);

        JScrollPane scrollPane = new JScrollPane(mainView);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(scrollPane);
        contentPane.add(controlPanel);

        //do i need this?
        setMinimumSize(new Dimension(0, 0));

        //get ready for action
        pack();
        setVisible(true);
    }
}
