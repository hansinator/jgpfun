package jgpfun;

import jgpfun.gui.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;

/**
 *
 * @author hansinator
 */
public class Main implements WindowListener {

    private volatile boolean running = true;

    private final Simulation sim;

    private final MainFrame frame;

    private final StatisticsHistoryModel statsHist = new StatisticsHistoryModel();

    private boolean slowMode = false;


    public Main(int width, int height) {
        sim = new Simulation(width, height, 48, 256, 40);
        //sim = new Simulation(width, height, 26, 256, 40);
        //sim = new Simulation(width, height, 32, 512, 40);

        frame = new MainFrame(width, height, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                slowMode = !slowMode;
                sim.setSlowMode(slowMode);
            }

        }, statsHist);

        System.out.println("MainView size: " + frame.mainView.getWidth() + "x" + frame.mainView.getHeight());
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main(2048, 1536).start();
    }


    private void start() {
        frame.addWindowListener(this);
        
        running = true;
        while (running) {
            //FIXME: add events to the simulation, so that a main view can draw upon an event
            sim.runGeneration(4000, statsHist, frame.mainView);
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
    
}
