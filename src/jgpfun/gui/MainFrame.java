package jgpfun.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author hansinator
 */
public class MainFrame extends JFrame {

    public MainView mainView;

    private JList foodList;

    private final JScrollPane centerPane;

    public final JPanel sidePaneLeft, sidePaneRight;

    public final BottomPanel bottomPane;


    public MainFrame(int width, int height, ActionListener speedListener, StatisticsHistoryModel statisticsHistory, XYSeries chartData) {
        super("BAH! Bonn!!1!11!!!");

        // create all direct clients
        centerPane = new JScrollPane();
        sidePaneLeft = new JPanel();
        sidePaneRight = new StatisticsHistoryPanel(statisticsHistory);
        bottomPane = new BottomPanel(speedListener, chartData);

        // set default center pane size properties
        centerPane.setPreferredSize(new Dimension(800, 600));

        // init application specific content
        initClientViews(width, height);

        // setup and add all stuff to the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(centerPane, BorderLayout.CENTER);
        contentPane.add(sidePaneLeft, BorderLayout.LINE_START);
        contentPane.add(sidePaneRight, BorderLayout.LINE_END);
        contentPane.add(bottomPane, BorderLayout.PAGE_END);

        // get ready for action
        pack();
        setVisible(true);
    }


    private void initClientViews(int width, int height) {
        // to be put elsewhere
        mainView = new MainView();
        mainView.setPreferredSize(new Dimension(width, height));

        setCenterPaneView(mainView);
    }


    public void setCenterPaneView(Component view) {
        centerPane.setViewportView(view);
    }

}
