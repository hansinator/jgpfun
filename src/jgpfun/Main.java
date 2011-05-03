package jgpfun;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.life.Simulation;
import jgpfun.life.AbstractPopulationManager;
import jgpfun.life.PopulationManager;
import jgpfun.util.Settings;
import java.io.File;
import jgpfun.genetics.Genome;
import jgpfun.genetics.lgp.EvoVM2;
import jgpfun.genetics.lgp.OpCode;
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
        
        try {
            EvoVM2.compile(32, Genome.randomGenome(256).program.toArray(new OpCode[0]));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        int worldWith = Settings.getInt("worldWidth"), worldHeight = Settings.getInt("worldHeight");
        World2d world = new World2d(worldWith, worldHeight, Settings.getInt("foodCount"));
        AbstractPopulationManager popMan = new PopulationManager(Settings.getInt("popSize"), Settings.getInt("progSize"));
        Simulation sim = new Simulation(world, popMan);
        new MainFrame(worldWith, worldHeight, sim).startSimulation();
    }

}
