package jgpfun.world2d;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import jgpfun.Food;
import jgpfun.util.Settings;

/**
 *
 * @author hansinator
 */
public class World2d {

    public static final int foodPickupRadius = Settings.getInt("foodPickupRadius");

    private final Random rnd;

    public final int worldWidth, worldHeight;

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


    public void moveOrganismInWorld(Organism2d organism, Object worldLock) {
        //TODO: have a more compex world, add a barrier in the middle of the screen
        //TODO: take into account ant size, so it can't hide outside of the screen
        //start = System.nanoTime();
        for (Body2d b : organism.bodies) {
            //prevent world wrapping
            //organism.dx = Math.min(Math.max(organism.dx, 0), worldWidth);
            //organism.dy = Math.min(Math.max(organism.dy, 0), worldHeight);

            //prevent world wrapping
            b.x = Math.min(Math.max(b.x, 0), worldWidth);
            b.y = Math.min(Math.max(b.y, 0), worldHeight);

            //eat food
            synchronized (worldLock) {
                if ((food.contains(b.food))
                        && (b.food.x >= (b.x - foodPickupRadius))
                        && (b.food.x <= (b.x + foodPickupRadius))
                        && (b.food.y >= (b.y - foodPickupRadius))
                        && (b.food.y <= (b.y + foodPickupRadius))) {
                    organism.incFood();
                    b.food.x = rnd.nextInt(worldWidth);
                    b.food.y = rnd.nextInt(worldHeight);
                }
            }
        }
        //System.out.println("Food computation took: " + (System.nanoTime() - start));
    }


    public void randomFood() {
        //FIXME: this seems to interfere with drawing, at least i'm getting an occasional concurrent list modification from within the food painting method
        //TEMP FIX - this might be faster than creating tons of food object every round
        if (food.size() != foodCount) {
            food.clear();
            for (int i = 0; i < foodCount; i++) {
                food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight)));
            }
        } else {
            for (Food f : food) {
                f.x = rnd.nextInt(worldWidth);
                f.y = rnd.nextInt(worldHeight);
            }
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
