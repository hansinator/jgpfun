package jgpfun.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import jgpfun.Simulation;

/**
 *
 * @author hansinator
 */
public class MainFrame extends JFrame {

    public MainView mainView;

    private final JScrollPane centerPane;

    public final JPanel sidePaneLeft, sidePaneRight;

    public final BottomPanel bottomPane;


    public MainFrame(int width, int height, final Simulation simulation) {
        super("BAH! Bonn!!1!11!!!");

        // create all direct clients
        JMenuBar menuBar = new JMenuBar();
        centerPane = new JScrollPane();
        sidePaneLeft = new JPanel();
        sidePaneRight = new StatisticsHistoryPanel(simulation.statisticsHistory);
        bottomPane = new BottomPanel(simulation);

        // populate menu bar
        menuBar.add(new JMenu("File")).add(new JMenuItem("Exit"));

        JMenu simulationMenu = new JMenu("Simulation");
        simulationMenu.add(new JMenuItem("Reset")).addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.reset();
            }

        });
        simulationMenu.add(new JMenuItem("Preferences"));
        menuBar.add(simulationMenu);

        // set default center pane size properties
        centerPane.setPreferredSize(new Dimension(800, 600));

        // init application specific content
        initClientViews(width, height);

        // add the menu bar
        setJMenuBar(menuBar);

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
