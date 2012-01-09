package de.hansinator.jgpfun;

import de.hansinator.jgpfun.life.Simulation;
import de.hansinator.jgpfun.life.AbstractPopulationManager;
import de.hansinator.jgpfun.life.PopulationManager;
import de.hansinator.jgpfun.util.Settings;
import java.io.File;
import de.hansinator.jgpfun.gui.MainFrame;
import de.hansinator.jgpfun.world2d.World2d;

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
        AbstractPopulationManager popMan = new PopulationManager(Settings.getInt("popSize"), Settings.getInt("progSize"));
        Simulation sim = new Simulation(world, popMan);
        new MainFrame(worldWith, worldHeight, sim).startSimulation();
    }

}
