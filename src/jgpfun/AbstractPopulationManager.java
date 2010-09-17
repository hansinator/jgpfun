package jgpfun;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public abstract class AbstractPopulationManager {

    public static final int foodTolerance = 10;

    public static final int maxMutations = 3;

    protected final Random rnd;

    protected final ThreadPoolExecutor pool;

    protected final int worldWidth, worldHeight;
    
    protected List<Organism> ants;

    protected World2d world;

    protected int gen = 0;

    protected boolean slowMode;

    public volatile int roundsMod = 800;
    

    public AbstractPopulationManager(int worldWidth, int worldHeight, int popSize, int foodCount) {
        rnd = new SecureRandom();
        
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        world = new World2d(worldWidth, worldHeight, foodCount);
        ants = new ArrayList<Organism>(popSize);

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public abstract void runGeneration(int iterations, MainView mainView, List<String> foodList);

    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

}
