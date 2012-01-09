package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.world2d.Organism2d;
import de.hansinator.fun.jgp.gui.MainView;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.hansinator.fun.jgp.gui.InfoPanel;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.world2d.World2d;
import org.jfree.data.xy.XYSeries;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

/**
 *
 * @author hansinator
 */
public class Simulation {

    //todo: have world object automatically add themselves to a legend that can be drawn onto the screen (bottom?)
    //todo: in a later simulation creation dialogue, have categories for "simple" stuff (bodiss, sesses) and more custom stuff.. a bit like the clonk menu?!

    //(it is questionable if this must be included in propertiers... it's fine if it's hardcoded for a while or two!)
    private int roundsMod = 800;

    //(it is questionable if this must be included in propertiers... it's fine if it's hardcoded for a while or two!)
    private int fpsMax = 70;

    private final ThreadPoolExecutor pool;

    private final Object lock = new Object();

    private int gen = 0;

    private boolean slowMode = false;

    private volatile boolean paused = false;

    public final World2d world;

    private final AbstractPopulationManager populationManager;

    public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

    public final XYSeries foodChartData = new XYSeries("fitness");

    public final XYSeries progSizeChartData = new XYSeries("prg size");

    public final XYSeries realProgSizeChartData = new XYSeries("real prg size");

    private volatile boolean running = true;

    private volatile boolean abort = false;

    private final Object runLock = new Object();

    public static final int ROUNDS_PER_GENERATION = 4000;


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


    //TODO: thread this or stuff
    public void start(MainView mainView, InfoPanel infoPanel) {
        int startGen = 0;
        long now, generationsPerMinuteAverage = 0, generationsPerMinuteCount = 0;
        running = true;
        abort = false;
        paused = false;

        System.out.println("Start time: " + DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));

        long startTime = System.currentTimeMillis();
        long lastStats = startTime;
        while (running) {
            //FIXME: add events to the simulation, so that a main view can draw upon an event
            runGeneration(ROUNDS_PER_GENERATION, mainView, infoPanel);

            //print generations per minute info
            now = System.currentTimeMillis();
            if ((now - lastStats) >= 3000) {
                long generationsPerMinute = (gen - startGen) * (60000 / (now - lastStats));
                generationsPerMinuteAverage += generationsPerMinute;
                generationsPerMinuteCount++;

                System.out.println("GPM: " + generationsPerMinute);

                System.out.println("Runtime: " + PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, now)));
                startGen = gen;
                lastStats = now;
            }
        }

        System.out.println("\nEnd time: " + DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));
        System.out.println("Runtime: " + PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, System.currentTimeMillis())));
        System.out.println("Average GPM: " + ((generationsPerMinuteCount > 0) ? (generationsPerMinuteAverage / generationsPerMinuteCount) : 0));
    }


    public void stop() {
        running = false;
    }


    public void runGeneration(int iterations, MainView view, InfoPanel infoPanel) {
        // scatter organisms in the world
        for (BaseOrganism organism : populationManager.organisms) {
            ((Organism2d) organism).addToWorld(world);
        }

        world.curOrganisms = populationManager.organisms;

        synchronized (runLock) {
            long start = System.currentTimeMillis();
            int lastStatRound = 0;

            for (int i = 0; i < iterations; i++) {
                while (paused) {
                    Thread.yield();
                }

                if (abort) {
                    break;
                }

                step();

                if (slowMode || (i % roundsMod) == 0) {
                    final long time = System.currentTimeMillis() - start;
                    start = System.currentTimeMillis();
                    final int rps = time > 0 ? (int) (((i - lastStatRound) * 1000) / time) : 1;
                    final int progress = (i * 100) / iterations;
                    lastStatRound = i;

                    infoPanel.updateInfo(rps, progress, gen + 1);
                    view.drawStuff(rps, progress);
                    view.repaint();

                    if (slowMode) {
                        if (time < (1000 / fpsMax)) {
                            try {
                                Thread.sleep((1000 / fpsMax) - time);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(PopulationManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
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
        final CountDownLatch cb = new CountDownLatch(populationManager.organisms.size());

        for (final BaseOrganism organism : populationManager.organisms) {
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
                    world.moveOrganismInWorld((Organism2d) organism, lock);

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
        this.roundsMod = roundsMod == 0 ? 1 : roundsMod;
    }


    public int getFps() {
        return fpsMax;
    }


    public void setFps(int fpsMax) {
        this.fpsMax = fpsMax == 0 ? 1 : fpsMax;
    }


    public int getGeneration() {
        return gen;
    }


    public void setPaused(boolean paused) {
        this.paused = paused;
    }


    public boolean isPausede() {
        return paused;
    }

}
