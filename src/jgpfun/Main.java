package jgpfun;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author hansinator
 */
public class Main implements WindowListener {

    private volatile boolean running = true;

    private final Simulation sim;

    private final MainFrame frame;

    private final List<String> foodHist = new ArrayList<String>();

    private boolean slowMode = false;


    public Main(int width, int height) {

        //FIXME: add events to the simulation, so that a main view can draw upon an event
        sim = new Simulation(width, height, 48, 256, 40, mainView);
        //sim = new Simulation(width, height, 26, 256, 40, mainView);
        //sim = new Simulation(width, height, 32, 512, 40, mainView);

        frame = new MainFrame(width, height, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                slowMode = !slowMode;
                sim.setSlowMode(slowMode);
            }

        }, new UpdatableListModel(foodHist));

        //maybe i'd put this somewhere else, as i'm leaking a reference
        //while in the constructor
        frame.addWindowListener(this);

        System.out.println("MainView size: " + frame.mainView.getWidth() + "x" + frame.mainView.getHeight());
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
            sim.runGeneration(4000, foodHist);
            frame.updateFoodList();
        }

        System.exit(0);
    }


    @Override
    public void windowOpened(WindowEvent e) {
    }


    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
    }


    @Override
    public void windowClosed(WindowEvent e) {
        running = false;
    }


    @Override
    public void windowIconified(WindowEvent e) {
    }


    @Override
    public void windowDeiconified(WindowEvent e) {
    }


    @Override
    public void windowActivated(WindowEvent e) {
    }


    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    public static class UpdatableListModel extends AbstractListModel {

        private final List l;


        public UpdatableListModel(List l) {
            this.l = l;
        }


        @Override
        public int getSize() {
            return l.size();
        }


        @Override
        public Object getElementAt(int index) {
            return l.get(index);
        }


        public void update() {
            fireContentsChanged(this, 0, l.size() - 1);
        }

    }
}
