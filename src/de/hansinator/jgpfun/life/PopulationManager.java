package de.hansinator.jgpfun.life;

import de.hansinator.jgpfun.genetics.Genome;
import de.hansinator.jgpfun.world2d.Organism2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hansinator.jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author hansinator
 */
public class PopulationManager extends AbstractPopulationManager {

    private int totalFit;


    public PopulationManager(int popSize, int progSize) {
        super(popSize, progSize);
    }


    @Override
    public void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData) {
        int avgProgSize = 0, avgRealProgSize = 0;

        for (BaseOrganism o : organisms) {
            avgProgSize += o.getGenome().size();
        }
        avgProgSize /= organisms.size();

        for (BaseOrganism o : organisms) {
            avgRealProgSize += ((Organism2d) o).vm.getProgramSize();
        }
        avgRealProgSize /= organisms.size();

        statisticsHistory.appendEntry(generation, totalFood, totalFood / organisms.size(), avgProgSize, avgRealProgSize);
        progSizeChartData.add(generation, avgProgSize);
        realProgSizeChartData.add(generation, avgRealProgSize);
    }


    @Override
    public int newGeneration() {
        double mutador;
        Genome child1, child2;
        BaseOrganism parent1, parent2;
        totalFit = calculateFitness();
        List<BaseOrganism> newAnts = new ArrayList<BaseOrganism>(organisms.size());

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (organisms.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = selector.select(organisms);
            parent2 = selector.select(organisms);
            child1 = parent1.getGenome().clone();
            child2 = parent2.getGenome().clone();

            //mutate or crossover with a user defined chance
            //mutador = rnd.nextDouble();
            //if (mutador > crossoverRate) {
            //mutate genomes
            child1.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            child2.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            /* else {
            //perform crossover
            crossover.cross(parent1, parent2, rnd);
            }*/

            //create new ants from the modified genomes and save them
            newAnts.add(child1.synthesize());
            newAnts.add(child2.synthesize());

            //add to genealogy tree
            genealogyTree.put(parent1.getGenome(), child1, parent1.getFitness());
            genealogyTree.put(parent1.getGenome(), child2, parent2.getFitness());
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
