package jgpfun;

import jgpfun.jgp.OpCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.util.EvoUtils;
import jgpfun.util.MutationUtils;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class PopulationManager extends AbstractPopulationManager {

    private final Object lock = new Object();


    public PopulationManager(World2d world, int popSize, int progSize) {
        super(world, popSize, progSize);
    }


    @Override
    public void printStats(long rps) {
        System.out.println("");
        System.out.println("GEN: " + gen);
        System.out.println("RPS: " + rps);

        int avgProgSize = 0;
        for (Organism o : ants) {
            avgProgSize += o.program.length;
        }
        avgProgSize /= ants.size();
        System.out.println("Avg prog size (current generation): " + avgProgSize);
    }


    @Override
    public void step() {
        final CountDownLatch cb = new CountDownLatch(ants.size());

        for (final Organism organism : ants) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    //long start = System.nanoTime();
                    //int oldx, oldy;

                    //find closest food

                    /*int x = 0, y = 0;
                    if(slowMode) {
                    x = organism.x;
                    y = organism.y;
                    }*/

                    //System.out.println("Find food took: " + (System.nanoTime() - start));
                    //start = System.nanoTime();

                    try {
                        organism.live();
                    } catch (Exception ex) {
                        Logger.getLogger(PopulationManager.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }

                    /*long live = (System.nanoTime() - start);
                    System.out.println("VM Run took: " + organism.vmrun);
                    System.out.println("Movement computation took: " + organism.comp);
                    System.out.println("All Run took: " + organism.allrun);
                    System.out.println("Live took: " + live);
                     */

                    /*if(slowMode) {
                    x = Math.abs(organism.x - x);
                    y = Math.abs(organism.y - y);

                    System.out.println("x " + x + "y " + y);
                    }*/

                    //move organism in world to see if it had hit some food or something like that
                    world.moveOrganismInWorld(organism, lock);

                    //start = System.nanoTime();
                    cb.countDown();
                    //System.out.println("Latch took: " + (System.nanoTime() - start));
                }

            };
            pool.execute(r);
        }
        try {
            cb.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(PopulationManager.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }


    @Override
    public int newGeneration() {
        int totalFit = calculateFitness();
        OpCode[] parent1, parent2;
        //double mutador;
        List<Organism> newAnts = new ArrayList<Organism>(ants.size());

        //choose crossover operator
        //CrossoverOperator crossOp = new TwoPointCrossover();

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = EvoUtils.rouletteWheel(ants, totalFit, rnd).clone();
            parent2 = EvoUtils.rouletteWheel(ants, totalFit, rnd).clone();

            //mutate or crossover with a user defined chance
            //mutador = rnd.NextDouble();
            //if (mutador > crossoverRate) {
            //mutate genomes
            parent1 = MutationUtils.mutate(parent1, rnd.nextInt(maxMutations) + 1, progSize, rnd);
            parent2 = MutationUtils.mutate(parent2, rnd.nextInt(maxMutations) + 1, progSize, rnd);
            /*} //crossover
            else {
            //perform crossover
            //(crossover operators automatically copy the genomes)
            crossOp.Cross(parent1, parent2, randomR);
            }*/

            //create new ants with the modified genomes and save them
            newAnts.add(new Organism(parent1, world.worldWidth, world.worldHeight, world.foodFinder));
            newAnts.add(new Organism(parent2, world.worldWidth, world.worldHeight, world.foodFinder));
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
