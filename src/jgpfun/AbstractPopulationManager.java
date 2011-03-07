package jgpfun;

import jgpfun.world2d.Organism2d;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author hansinator
 */
public abstract class AbstractPopulationManager {

    /*
     * The chance with which crossover happens, rest is mutation.
     */
    public static final double crossoverRate = Settings.getDouble("crossoverRate");

    public static final int maxMutations = Settings.getInt("maxMutations");

    protected final Random rnd;

    protected final int progSize;

    protected final int popSize;

    protected List<Organism2d> ants;

    protected final World2d world;


    public AbstractPopulationManager(World2d world, int popSize, int progSize) {
        this.world = world;
        this.progSize = progSize;
        this.popSize = popSize;

        ants = new ArrayList<Organism2d>(popSize);
        rnd = new SecureRandom();

        for (int i = 0; i < popSize; i++) {
            ants.add(new Organism2d(Genome.randomGenome(progSize), world));
        }
    }


    public void reset() {
        ants.clear();
        
        for (int i = 0; i < popSize; i++) {
            ants.add(new Organism2d(Genome.randomGenome(progSize), world));
        }
    }


    public abstract void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData);


    public abstract int newGeneration();


    public List<Organism2d> getAnts() {
        return ants;
    }

}
