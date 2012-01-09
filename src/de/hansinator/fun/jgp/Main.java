package de.hansinator.fun.jgp;

import de.hansinator.fun.jgp.life.Simulation;
import de.hansinator.fun.jgp.life.AbstractPopulationManager;
import de.hansinator.fun.jgp.life.PopulationManager;
import de.hansinator.fun.jgp.util.Settings;
import java.io.File;
import de.hansinator.fun.jgp.gui.MainFrame;
import de.hansinator.fun.jgp.world2d.World2d;

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
