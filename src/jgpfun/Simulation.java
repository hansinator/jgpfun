package jgpfun;

import jgpfun.gui.MainView;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.gui.InfoPanel;
import jgpfun.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import jgpfun.world2d.World2d;
import org.jfree.data.xy.XYSeries;

//TODO: add a generations per second/minute output
/**
 *
 * @author hansinator
 */
public class Simulation {

    //(it is questionable if this must be included in propertiers... it's fine if it's hardcoded for a while or two!)
    private int roundsMod = 800;

    private final ThreadPoolExecutor pool;

    private final Object lock = new Object();

    private int gen = 0;

    private boolean slowMode;

    private final World2d world;

    private final AbstractPopulationManager populationManager;

    public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

    public final XYSeries foodChartData = new XYSeries("fitness");

    public final XYSeries progSizeChartData = new XYSeries("prg size");

    public final XYSeries realProgSizeChartData = new XYSeries("real prg size");

    private volatile boolean abort = false;

    private final Object runLock = new Object();


    public Simulation(World2d world, AbstractPopulationManager populationManager) {
        this.world = world;
        this.populationManager = populationManager;

        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        foodChartData.setMaximumItemCount(500);
        progSizeChartData.setMaximumItemCount(500);
        realProgSizeChartData.setMaximumItemCount(500);
    }


    public void reset() {
        abort = true;
        synchronized (runLock) {
            populationManager.reset();
            world.randomFood();
        }
        gen = 0;
        foodChartData.clear();
        progSizeChartData.clear();
        realProgSizeChartData.clear();
        statisticsHistory.clear();
        abort = false;
    }


    public void runGeneration(int iterations, MainView view, InfoPanel infoPanel) {
        long start = System.currentTimeMillis();
        long time;

        synchronized (runLock) {
            for (int i = 0; i < iterations; i++) {
                if (abort) {
                    break;
                }

                step();

                if (slowMode || (i % roundsMod) == 0) {
                    time = System.currentTimeMillis() - start;
                    infoPanel.updateInfo(time > 0 ? (int) ((i * 1000) / time) : 1, (i * 100) / iterations, gen + 1);
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

            // simulation statistics
            System.out.println("");
            System.out.println("GEN: " + gen);
            System.out.println("RPS: " + (iterations * 1000) / (System.currentTimeMillis() - start));

            // population statistics
            populationManager.printStats(statisticsHistory, foodCollected, gen, progSizeChartData, realProgSizeChartData);
            foodChartData.add(gen, foodCollected);

            world.randomFood();
        }
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


    public int getRoundsMod() {
        return roundsMod;
    }


    public void setRoundsMod(int roundsMod) {
        this.roundsMod = roundsMod;
    }


    public int getGeneration() {
        return gen;
    }

}