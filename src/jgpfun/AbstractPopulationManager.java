package jgpfun;

import jgpfun.util.Settings;
import jgpfun.world2d.Organism2d;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.genetics.crossover.CrossoverOperator;
import jgpfun.genetics.crossover.OffsetTwoPointCrossover;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.genetics.selection.SelectionStrategy;
import jgpfun.genetics.selection.TournamentSelector;
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

    protected List<BaseOrganism> organisms;

    protected final World2d world;

    protected final SelectionStrategy selector = new TournamentSelector(3);

    protected final CrossoverOperator crossover;


    public AbstractPopulationManager(World2d world, int popSize, int progSize) {
        this.world = world;
        this.progSize = progSize;
        this.popSize = popSize;
        
        crossover = new OffsetTwoPointCrossover(progSize / 8);
        organisms = new ArrayList<BaseOrganism>(popSize);
        rnd = new SecureRandom();

        for (int i = 0; i < popSize; i++) {
            organisms.add(Genome.randomGenome(progSize).synthesize(world));
        }
    }


    public void reset() {
        organisms.clear();
        
        for (int i = 0; i < popSize; i++) {
            organisms.add(new Organism2d(Genome.randomGenome(progSize), world));
        }
    }


    public abstract void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData);


    public abstract int newGeneration();


    public abstract int getCurrentPopulationFitness();


    public List<BaseOrganism> getOrganisms() {
        return organisms;
    }

}
