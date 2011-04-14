package jgpfun;

import jgpfun.genetics.Genome;
import jgpfun.world2d.Organism2d;
import java.util.ArrayList;
import java.util.List;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author hansinator
 */
public class PopulationManager extends AbstractPopulationManager {

    private int totalFit;

    public PopulationManager(World2d world, int popSize, int progSize) {
        super(world, popSize, progSize);
    }


    @Override
    public void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData) {
        int avgProgSize = 0, avgRealProgSize = 0;

        for (BaseOrganism o : organisms) {
            avgProgSize += o.getGenome().size();
        }
        avgProgSize /= organisms.size();

        for (BaseOrganism o : organisms) {
            avgRealProgSize += ((Organism2d)o).vm.getProgramSize();
        }
        avgRealProgSize /= organisms.size();

        statisticsHistory.appendEntry(generation, totalFood, totalFood / organisms.size(), avgProgSize, avgRealProgSize);
        progSizeChartData.add(generation, avgProgSize);
        realProgSizeChartData.add(generation, avgRealProgSize);
    }


    @Override
    public int newGeneration() {
        double mutador;
        Genome parent1, parent2;
        totalFit = calculateFitness();
        List<BaseOrganism> newAnts = new ArrayList<BaseOrganism>(organisms.size());

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (organisms.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = selector.select(organisms).getGenome().clone();
            parent2 = selector.select(organisms).getGenome().clone();

            //mutate or crossover with a user defined chance
            //mutador = rnd.nextDouble();
            //if (mutador > crossoverRate) {
            //mutate genomes
            parent1.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            parent2.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            /* else {
            //perform crossover
            crossover.cross(parent1, parent2, rnd);
            }*/

            //create new ants from the modified genomes and save them
            newAnts.add(parent1.synthesize(world));
            newAnts.add(parent2.synthesize(world));
        }

        //replace and leave the other to GC
        organisms = newAnts;

        return totalFit;
    }


    //the team effort
    private int calculateFitness() {
        int totalFit = 0;
        for (BaseOrganism o : organisms) {
            totalFit += o.getFitness();
        }
        return totalFit;
    }


    @Override
    public int getCurrentPopulationFitness() {
        return totalFit;
    }

}
