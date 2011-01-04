package jgpfun;

import jgpfun.gui.MainFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

/**
 *
 * @author hansinator
 */
public class Main implements WindowListener {

    private volatile boolean running = true;

    private final Simulation sim;

    private final MainFrame frame;


    public Main(int width, int height) {
        sim = new Simulation(width, height, 48, 256, 40);
        //sim = new Simulation(width, height, 26, 256, 40);
        //sim = new Simulation(width, height, 32, 512, 40);

        frame = new MainFrame(width, height, sim);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main(2048, 1536).start();
    }


    private void start() {
        System.out.println("MainView size: " + frame.mainView.getWidth() + "x" + frame.mainView.getHeight());
        System.out.println("Start time: " + new Date());

        frame.addWindowListener(this);

        running = true;
        while (running) {
            //FIXME: add events to the simulation, so that a main view can draw upon an event
            sim.runGeneration(4000, frame.mainView, frame.bottomPane.infoPanel);
        }

        System.exit(0);
    }


    @Override
    public void windowOpened(WindowEvent e) {
    }


    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
        sim.reset();
    }


    @Override
    public void windowClosed(WindowEvent e) {
        running = false;
        sim.reset();
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

}
