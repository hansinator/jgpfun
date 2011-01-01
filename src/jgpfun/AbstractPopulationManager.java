package jgpfun;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public abstract class AbstractPopulationManager {

    /*
     * The chance with which crossover happens, rest is mutation.
     */
    public static final double crossoverRate = 0.2;

    public static final int foodTolerance = 10;

    public static final int maxMutations = 3;

    protected final Random rnd;

    protected final int progSize;

    protected List<Organism> ants;

    protected final World2d world;


    public AbstractPopulationManager(World2d world, int popSize, int progSize) {
        this.world = world;
        this.progSize = progSize;

        ants = new ArrayList<Organism>(popSize);
        rnd = new SecureRandom();

        for (int i = 0; i < popSize; i++) {
            ants.add(Organism.randomOrganism(world, progSize));
        }
    }


    public abstract void printStats(StatisticsHistoryModel statsHistory, int totalFood, int generation);


    public abstract int newGeneration();


    public List<Organism> getAnts() {
        return ants;
    }

}
