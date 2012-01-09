package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.util.Settings;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
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

    protected final SelectionStrategy selector = new TournamentSelector(3);

    protected final CrossoverOperator crossover;

    protected final GenealogyTree genealogyTree;


    public AbstractPopulationManager(int popSize, int progSize) {
        this.progSize = progSize;
        this.popSize = popSize;

        genealogyTree = new GenealogyTree();
        crossover = new OffsetTwoPointCrossover(progSize / 8);
        organisms = new ArrayList<BaseOrganism>(popSize);
        rnd = new SecureRandom();

        reset();
    }


    public final void reset() {
        genealogyTree.clear();
        organisms.clear();
        for (int i = 0; i < popSize; i++) {
            Genome g = Genome.randomGenome(progSize);
            organisms.add(g.synthesize());
            genealogyTree.put(g);
        }
    }


    public abstract void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData);


    public abstract int newGeneration();


    public abstract int getCurrentPopulationFitness();


    public List<BaseOrganism> getOrganisms() {
        return organisms;
    }

}