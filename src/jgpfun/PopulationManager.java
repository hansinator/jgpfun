package jgpfun;

import jgpfun.jgp.OpCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jgpfun.crossover.CrossoverOperator;
import jgpfun.crossover.OnePointCrossover;
import jgpfun.util.EvoUtils;
import jgpfun.util.MutationUtils;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class PopulationManager extends AbstractPopulationManager {

    public PopulationManager(World2d world, int popSize, int progSize) {
        super(world, popSize, progSize);
    }


    @Override
    public void printStats(long rps) {
        System.out.println("RPS: " + rps);

        int avgProgSize = 0;
        for (Organism o : ants) {
            avgProgSize += o.program.length;
        }
        avgProgSize /= ants.size();
        System.out.println("Avg prog size: " + avgProgSize);

        avgProgSize = 0;
        for (Organism o : ants) {
            avgProgSize += o.vm.getProgramSize();
        }
        avgProgSize /= ants.size();
        System.out.println("Avg real prog size: " + avgProgSize);
    }


    @Override
    public int newGeneration() {
        double mutador;
        int totalFit = calculateFitness();
        List<Organism> newAnts = new ArrayList<Organism>(ants.size());

        //choose crossover operator
        CrossoverOperator crossOp = new OnePointCrossover();

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {
            List<OpCode> parent1 = new ArrayList<OpCode>(), parent2 = new ArrayList<OpCode>();
            
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1.addAll(Arrays.asList(EvoUtils.rouletteWheel(ants, totalFit, rnd).clone()));
            parent2.addAll(Arrays.asList(EvoUtils.rouletteWheel(ants, totalFit, rnd).clone()));

            //mutate or crossover with a user defined chance
            mutador = rnd.nextDouble();
            if (mutador > crossoverRate) {
                //mutate genomes
                MutationUtils.mutate(parent1, rnd.nextInt(maxMutations) + 1, progSize, rnd);
                MutationUtils.mutate(parent2, rnd.nextInt(maxMutations) + 1, progSize, rnd);
            } else {
                //perform crossover
                crossOp.cross(parent1, parent2, rnd);
            }

            //create new ants with the modified genomes and save them
            newAnts.add(new Organism(parent1.toArray(new OpCode[parent1.size()]), world.worldWidth, world.worldHeight, world.foodFinder));
            newAnts.add(new Organism(parent2.toArray(new OpCode[parent2.size()]), world.worldWidth, world.worldHeight, world.foodFinder));
        }

        //replace and leave the other to GC
        ants = newAnts;

        return totalFit;
    }


    //the team effort
    private int calculateFitness() {
        int totalFit = 0;
        for (Organism o : ants) {
            totalFit += o.food;
        }
        return totalFit;
    }

}
