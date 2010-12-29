package jgpfun;

import jgpfun.gui.MainView;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;

//TODO: add a generations per second/minute output

/**
 *
 * @author hansinator
 */
public class Simulation {

    private static int roundsMod = 800;

    private final ThreadPoolExecutor pool;

    private final Object lock = new Object();

    private int gen = 0;

    private boolean slowMode;

    private final World2d world;

    private final AbstractPopulationManager populationManager;


    public Simulation(int worldWidth, int worldHeight, int popSize, int progSize, int foodCount) {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        world = new World2d(worldWidth, worldHeight, foodCount);
        populationManager = new PopulationManager(world, popSize, progSize);
    }


    public void runGeneration(int iterations, StatisticsHistoryModel statsHist, MainView view) {
        long start = System.currentTimeMillis();
        long time;

        for (int i = 0; i < iterations; i++) {
            step();

            if (slowMode || (i % roundsMod) == 0) {
                time = System.currentTimeMillis() - start;
                view.drawStuff(world.food, populationManager.ants, time > 0 ? (int) ((i * 1000) / time) : 1, (i * 100) / iterations);
                view.repaint();

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

        int foodCollected = populationManager.newGeneration();
        statsHist.appendEntry(gen, foodCollected, 0, 0, 0);

        System.out.println("");
        System.out.println("GEN: " + gen);
        populationManager.printStats((iterations * 1000) / (System.currentTimeMillis() - start));

        world.randomFood();
    }


    private void step() {
        final CountDownLatch cb = new CountDownLatch(populationManager.ants.size());

        for (final Organism organism : populationManager.ants) {
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
                        Logger.getLogger(PoolingPopulationManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PoolingPopulationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

}
