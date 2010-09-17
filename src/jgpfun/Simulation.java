package jgpfun;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class Simulation {

    private static int roundsMod = 800;

    private final MainView mainView;

    private int gen = 0;

    private boolean slowMode;

    private final World2d world;

    private final AbstractPopulationManager populationManager;


    public Simulation(int worldWidth, int worldHeight, int popSize, int progSize, int foodCount, MainView mainView) {
        world = new World2d(worldWidth, worldHeight, foodCount);
        populationManager = new PopulationManager(world, popSize, progSize);
        this.mainView = mainView;
    }


    public void runGeneration(int iterations, List<String> foodList) {
        long start = System.currentTimeMillis();
        long time;

        for (int i = 0; i < iterations; i++) {
            populationManager.step();
            
            if (slowMode || (i % roundsMod) == 0) {
                time = System.currentTimeMillis() - start;
                mainView.drawStuff(world.food, populationManager.ants, time > 0 ? (int) ((i * 1000) / time) : 1, (i * 100) / iterations);
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

        int foodCollected = populationManager.newGeneration();
        foodList.add(0, "Food: " + foodCollected);

        populationManager.printStats((iterations * 1000) / (System.currentTimeMillis() - start));

        world.randomFood();
    }


    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

}
