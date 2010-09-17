package jgpfun;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hansinator
 */
public class Simulation {

    private static int roundsMod = 800;

    private int gen = 0;

    private boolean slowMode;


    public Simulation() {
    }


    public void runGeneration(int iterations, MainView mainView, List<String> foodList) {
        long start = System.currentTimeMillis();
        long time;

        for (int i = 0; i < iterations; i++) {
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
        foodList.add(0, "Food: " + foodCollected);

        world.randomFood();
    }


    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

}
