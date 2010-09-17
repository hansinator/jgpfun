package jgpfun;

import jgpfun.jgp.OpCode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.util.EvoUtils;
import jgpfun.util.MutationUtils;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class PoolingPopulationManager {

    private final Random rnd;

    private List<Organism> ants;

    private List<Organism> organismPool;

    private final int worldWidth, worldHeight;

    private World2d world;

    public static final int foodTolerance = 10;

    public static final int maxMutations = 4;

    public static final int maxPoolSize = 52;

    private final int progSize;

    private boolean slowMode;

    private int bestInPool;

    private final Object lock = new Object();

    private final ThreadPoolExecutor pool;

    public volatile int roundsMod = 800;


    public PoolingPopulationManager(int worldWidth, int worldHeight, int popSize, int progSize, int foodCount) {
        ants = new ArrayList<Organism>(popSize);
        rnd = new SecureRandom();
        world = new World2d(worldWidth, worldHeight, foodCount);
        organismPool = new ArrayList<Organism>(maxPoolSize);

        for (int i = 0; i < popSize; i++) {
            ants.add(Organism.randomOrganism(worldWidth, worldHeight, progSize, world.foodFinder));
        }

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.progSize = progSize;

        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    static int gen = 0;


    public void runGeneration(int iterations, MainView mainView, List<Integer> foodList) {
        long start = System.currentTimeMillis();
        long time;

        for (int i = 0; i < iterations; i++) {
            //ants.get(0).showdebug = (roundsMod == 1);
            step();
            if (slowMode || (i % roundsMod) == 0) {
                time = System.currentTimeMillis() - start;
                mainView.drawStuff(world.food, ants, time > 0 ? (int) ((i * 1000) / time) : 1, (i * 100) / iterations);
                mainView.repaint();

                if (slowMode) {
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PopulationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        gen++;

        System.out.println("");
        System.out.println("GEN: " + gen);
        System.out.println("RPS: " + ((iterations * 1000) / (System.currentTimeMillis() - start)));

        int avgProgSize = 0;
        for (Organism o : ants) {
            avgProgSize += o.program.length;
        }
        avgProgSize /= ants.size();
        System.out.println("Avg prog size (current generation): " + avgProgSize);

        int foodCollected = newGeneration();
        foodList.add(0, foodCollected);

        System.out.println("Pool size: " + organismPool.size());
        System.out.println("Best in pool: " + bestInPool);

        world.randomFood();
    }

    private void printPool() {
        System.out.println("Pool:");
        for(int i = 0; i < organismPool.size(); i++) {
            System.out.println("" + i + ":\t" + organismPool.get(i).food);
        }
    }


    void step() {
        final CountDownLatch cb = new CountDownLatch(ants.size());

        for (final Organism organism : ants) {
            Runnable r = new Runnable() {

                public void run() {
                    try {
                        organism.live();
                    } catch (Exception ex) {
                        Logger.getLogger(PoolingPopulationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //move organism in world to see if it had hit some food or something like that
                    world.moveOrganismInWorld(organism, lock);

                    cb.countDown();
                }

            };

            pool.execute(r);
        }
        try {
            cb.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(PoolingPopulationManager.class.getName()).log(Level.SEVERE, null, ex);
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

            //reshuffle to make roulettewheel work better
            Collections.shuffle(organismPool);
            Collections.shuffle(organismPool);
        }
    }


    private int newGeneration() {
        int totalFit, currentGenerationFood;
        OpCode[] parent1, parent2;
        double mutador;
        List<Organism> newAnts = new ArrayList<Organism>(ants.size());

        //enqueue all current organisms into our pool
        organismPool.addAll(ants);

        //update the pool
        updatePool();

        //get the fitness
        //call order is important, because of:
        //TODO: global variable bestInPool...
        currentGenerationFood = calculateFitness(ants);
        totalFit = calculateFitness(organismPool);

        //choose crossover operator
        //CrossoverOperator crossOp = new TwoPointCrossover();

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = EvoUtils.rouletteWheel(organismPool, totalFit, rnd).clone();
            parent2 = EvoUtils.rouletteWheel(organismPool, totalFit, rnd).clone();

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
            newAnts.add(new Organism(parent1, worldWidth, worldHeight, world.foodFinder));
            newAnts.add(new Organism(parent2, worldWidth, worldHeight, world.foodFinder));
        }

        //replace and leave the other to GC
        ants = newAnts;

        return currentGenerationFood;
    }


    //the team effort
    private int calculateFitness(List<Organism> organisms) {
        int totalFit = 0;
        bestInPool = 0;

        for (Organism o : organisms) {
            totalFit += o.food;

            //remember the best
            if (o.food > bestInPool) {
                bestInPool = o.food;
            }
        }

        return totalFit;
    }


    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }
}
