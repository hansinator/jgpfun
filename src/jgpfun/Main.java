package jgpfun;

import java.io.File;
import jgpfun.gui.MainFrame;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class Main {

    public static void startSimulation(int worldWith, int worldHeight) {
        World2d world = new World2d(worldWith, worldHeight, Settings.getInt("foodCount"));
        AbstractPopulationManager popMan = new PopulationManager(world, Settings.getInt("popSize"), Settings.getInt("progSize"));
        Simulation sim = new Simulation(world, popMan);
        new MainFrame(worldWith, worldHeight, sim).startSimulation();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Settings.load(new File("default.properties"));
        startSimulation(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"));
    }

}
