package jgpfun;

import jgpfun.gui.MainFrame;

/**
 *
 * @author hansinator
 */
public class Main {

    public static void startSimulation(int width, int height) {
        Simulation sim = new Simulation(width, height, 48, 256, 40);
        //sim = new Simulation(width, height, 26, 256, 40);
        //sim = new Simulation(width, height, 32, 512, 40);

        new MainFrame(width, height, sim).startSimulation();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startSimulation(2048, 1536);
    }

}
