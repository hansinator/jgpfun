package jgpfun;

import jgpfun.util.Settings;
import java.io.File;
import jgpfun.gui.MainFrame;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Settings.load(new File("default.properties"));

        int worldWith = Settings.getInt("worldWidth"), worldHeight = Settings.getInt("worldHeight");
        World2d world = new World2d(worldWith, worldHeight, Settings.getInt("foodCount"));
        AbstractPopulationManager popMan = new PopulationManager(world, Settings.getInt("popSize"), Settings.getInt("progSize"));
        Simulation sim = new Simulation(world, popMan);
        new MainFrame(worldWith, worldHeight, sim).startSimulation();
    }

}
