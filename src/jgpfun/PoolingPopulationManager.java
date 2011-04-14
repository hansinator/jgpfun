package jgpfun;

import jgpfun.world2d.Organism2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jgpfun.crossover.CrossoverOperator;
import jgpfun.crossover.TwoPointCrossover;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author hansinator
 */
public class PoolingPopulationManager extends AbstractPopulationManager {

    public static final int maxPoolSize = 26;

    private List<Organism2d> organismPool;

    private int bestInPool;

    private int foodCollected;

    private int totalFit;


    public PoolingPopulationManager(World2d world, int popSize, int progSize) {
        super(world, popSize, progSize);
        organismPool = new ArrayList<Organism2d>(maxPoolSize);
    }


    @Override
    public void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation, XYSeries progSizeChartData, XYSeries realProgSizeChartData) {
        int avgProgSize = 0, avgRealProgSize = 0;

        // pool statistics
        for (Organism2d o : organismPool) {
            avgProgSize += o.getGenome().program.size();
        }
        avgProgSize /= (organismPool.size() > 0) ? organismPool.size() : 1;

        for (Organism2d o : organismPool) {
            avgRealProgSize += o.vm.getProgramSize();
        }
        avgRealProgSize /= (organismPool.size() > 0) ? organismPool.size() : 1;

        System.out.println("Avg pool prg size (cur gen): " + avgProgSize);
        System.out.println("Avg real pool prg size (cur gen): " + avgRealProgSize);
        System.out.println("Pool food: " + totalFit);
        System.out.println("Pool avg food: " + (totalFit / ((organismPool.size() > 0) ? organismPool.size() : 1)));
        System.out.println("Best in pool: " + bestInPool);

        
        // generation statistics
        avgProgSize = 0;
        for (Organism2d o : ants) {
            avgProgSize += o.getGenome().program.size();
        }
        avgProgSize /= ants.size();

        avgRealProgSize = 0;
        for (Organism2d o : ants) {
            avgRealProgSize += o.vm.getProgramSize();
        }
        avgRealProgSize /= ants.size();

        statisticsHistory.appendEntry(generation, totalFood, totalFood / ants.size(), avgProgSize, avgRealProgSize);
        progSizeChartData.add(generation, avgProgSize);
        realProgSizeChartData.add(generation, avgRealProgSize);
    }


    private void printPool() {
        System.out.println("Pool:");
        for (int i = 0; i < organismPool.size(); i++) {
            System.out.println("" + i + ":\t" + organismPool.get(i).getFitness());
        }
    }


    //this funtion ensures that the populatio pool
    //does not exceed it's maximum size by purging
    //the least successful organisms

    private void updatePool() {
        if (organismPool.size() > maxPoolSize) {
            //sort the list so that the fittest organisms are on top
            Collections.sort(organismPool);
            Collections.reverse(organismPool);

            //drop all superfluous organisms
            for (int i = organismPool.size() - 1; i > (maxPoolSize - 1); i--) {
                organismPool.remove(i);
            }
        }
    }


    @Override
    public int newGeneration() {
        double mutador;
        Genome parent1, parent2;
        List<Organism2d> newAnts = new ArrayList<Organism2d>(ants.size());

        //enqueue all current organisms into our pool
        organismPool.addAll(ants);

        //get the fitness
        //call order is important, because of:
        //TODO: global variable bestInPool...
        foodCollected = calculateFitness(ants);
        totalFit = calculateFitness(organismPool);

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {   
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = selector.select(organismPool).getGenome().clone();
            parent2 = selector.select(organismPool).getGenome().clone();

            //mutate or crossover with a user defined chance
            mutador = rnd.nextDouble();
            //if (mutador > crossoverRate) {
                //mutate genomes
                parent1.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
                parent2.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
            /*}
            else {
                //perform crossover
                crossover.cross(parent1, parent2, rnd);
            }*/

            //create new ants from the modified genomes and save them
            newAnts.add(parent1.synthesize(world));
            newAnts.add(parent2.synthesize(world));
        }

        //replace and leave the other to GC
        ants = newAnts;

        //update the pool
        updatePool();

        return foodCollected;
    }


    //the team effort
    private int calculateFitness(List<Organism2d> organisms) {
        int totalFit = 0;
        bestInPool = 0;

        for (Organism2d o : organisms) {
            totalFit += o.getFitness();

            //remember the best
            if (o.getFitness() > bestInPool) {
                bestInPool = o.getFitness();
            }
        }

        return totalFit;
    }


    @Override
    public int getCurrentPopulationFitness() {
        return totalFit;
    }

}
