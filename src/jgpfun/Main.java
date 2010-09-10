package jgpfun;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author hansinator
 */
public class Main implements WindowListener {

    private volatile boolean running = true;

    private final PopulationManager pm;

    private final JFrame frame;

    private final MainView mainView;

    private final JList foodList;

    private final List<Integer> foodHist = new ArrayList<Integer>();

    private boolean slowMode = false;


    public Main(int width, int height) {
        pm = new PopulationManager(width, height, 26, 256, 40);

        mainView = new MainView();
        mainView.setPreferredSize(new Dimension(width, height));

        foodList = new JList(new UpdatableListModel(foodHist));
        foodList.setPreferredSize(new Dimension(200, 0));
        foodList.setAlignmentY(Container.TOP_ALIGNMENT);

        JButton speedSwitch = new JButton("fast/slow");
        speedSwitch.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                slowMode = !slowMode;
                pm.setSlowMode(slowMode);
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setAlignmentY(Container.TOP_ALIGNMENT);
        controlPanel.add(speedSwitch);
        controlPanel.add(foodList);

        JScrollPane scrollPane = new JScrollPane(mainView);

        frame = new JFrame("BAH! Bonn!!1!11!!!");

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(scrollPane);
        contentPane.add(controlPanel);

        frame.setMinimumSize(new Dimension(0, 0));
        frame.pack();
        frame.setVisible(true);

        //maybe i'd put this somewhere else, as i'm leaking a reference
        //while in the constructor
        frame.addWindowListener(this);

        System.out.println("MainView size: " + mainView.getWidth() + "x" + mainView.getHeight());
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main(2048, 1536).start();
    }


    private void start() {
        running = true;
        while (running) {
            pm.runGeneration(4000, mainView, foodHist);
            ((UpdatableListModel) foodList.getModel()).update();
        }

        System.exit(0);
    }


    public void windowOpened(WindowEvent e) {
    }


    public void windowClosing(WindowEvent e) {
        running = false;
    }


    public void windowClosed(WindowEvent e) {
        running = false;
    }


    public void windowIconified(WindowEvent e) {
    }


    public void windowDeiconified(WindowEvent e) {
    }


    public void windowActivated(WindowEvent e) {
    }


    public void windowDeactivated(WindowEvent e) {
    }

    private class UpdatableListModel extends AbstractListModel {

        private final List l;


        public UpdatableListModel(List l) {
            this.l = l;
        }


        public int getSize() {
            return l.size();
        }


        public Object getElementAt(int index) {
            return l.get(index);
        }


        public void update() {
            fireContentsChanged(this, 0, l.size() - 1);
        }

    }
}