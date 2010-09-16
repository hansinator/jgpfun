package jgpfun.world2d;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import jgpfun.Food;
import jgpfun.Organism;
import jgpfun.PopulationManager;

/**
 *
 * @author hansinator
 */
public class World2d {

    private final Random rnd;

    private final int worldWidth, worldHeight;

    public final List<Food> food;

    public final FoodFinder foodFinder;

    private final int foodCount;


    public World2d(int worldWidth, int worldHeight, int foodCount) {
        rnd = new SecureRandom();

        food = new ArrayList<Food>(foodCount);
        foodFinder = new FoodFinder(Collections.unmodifiableList(food));
        randomFood();

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.foodCount = foodCount;
    }


    public void moveOrganismInWorld(Organism organism, Object worldLock) {
        //TODO: have a more compex world, add a barrier in the middle of the screen
        //TODO: take into account ant size, so it can't hide outside of the screen
        //prevent world wrapping
        //start = System.nanoTime();
        for (Body2d b : organism.bodies) {
            b.x = Math.min(Math.max(b.x, 0), worldWidth);
            b.y = Math.min(Math.max(b.y, 0), worldHeight);

            //eat food
            synchronized (worldLock) {
                if ((food.contains(b.food))
                        && (b.food.x >= (b.x - PopulationManager.foodTolerance))
                        && (b.food.x <= (b.x + PopulationManager.foodTolerance))
                        && (b.food.y >= (b.y - PopulationManager.foodTolerance))
                        && (b.food.y <= (b.y + PopulationManager.foodTolerance))) {
                    organism.food++;
                    b.food.x = rnd.nextInt(worldWidth);
                    b.food.y = rnd.nextInt(worldHeight);
                }
            }
        }
        //System.out.println("Food computation took: " + (System.nanoTime() - start));
    }


    public void randomFood() {
        food.clear();
        for (int i = 0; i < foodCount; i++) {
            food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight)));
        }
    }


    private boolean checkBarrier(int inx, int iny, int x1, int y1, int x2,
            int y2) {
        if (((inx >= x1) && (inx <= x2)) && ((iny >= y1) && (iny <= y2))) {
            return false;
        }
        return true;
    }

}
