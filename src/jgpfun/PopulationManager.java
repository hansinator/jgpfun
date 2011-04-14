package jgpfun;

import jgpfun.world2d.Organism2d;
import java.util.ArrayList;
import java.util.List;
import jgpfun.crossover.CrossoverOperator;
import jgpfun.crossover.OffsetTwoPointCrossover;
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

        for (Organism2d o : ants) {
            avgProgSize += o.getGenome().size();
        }
        avgProgSize /= ants.size();

        for (Organism2d o : ants) {
            avgRealProgSize += o.vm.getProgramSize();
        }
        avgRealProgSize /= ants.size();

        statisticsHistory.appendEntry(generation, totalFood, totalFood / ants.size(), avgProgSize, avgRealProgSize);
        progSizeChartData.add(generation, avgProgSize);
        realProgSizeChartData.add(generation, avgRealProgSize);
    }


    @Override
    public int newGeneration() {
        double mutador;
        Genome parent1, parent2;
        totalFit = calculateFitness();
        List<Organism2d> newAnts = new ArrayList<Organism2d>(ants.size());

        //choose crossover operator
        CrossoverOperator crossOp = new OffsetTwoPointCrossover(progSize / 8);

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = selector.select(ants).getGenome().clone();
            parent2 = selector.select(ants).getGenome().clone();

            //mutate or crossover with a user defined chance
            //mutador = rnd.nextDouble();
            //if (mutador > crossoverRate) {
            //mutate genomes
            parent1.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            parent2.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            /* else {
            //perform crossover
            crossOp.cross(parent1, parent2, rnd);
            }*/

            //create new ants with the modified genomes and save them
            newAnts.add(new Organism2d(parent1, world));
            newAnts.add(new Organism2d(parent2, world));
        }

        //replace and leave the other to GC
        ants = newAnts;

        return totalFit;
    }


    //the team effort
    private int calculateFitness() {
        int totalFit = 0;
        for (Organism2d o : ants) {
            totalFit += o.getFitness();
        }
        return totalFit;
    }


    @Override
    public int getCurrentPopulationFitness() {
        return totalFit;
    }

}
