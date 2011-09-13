package jgpfun.world2d;

import java.awt.Graphics;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.life.BaseOrganism;

/**
 *
 * @author hansinator
 */
public class World2d {

    private final Random rnd;

    public final int worldWidth, worldHeight;

    public List<BaseOrganism> curOrganisms;

    public final List<Food> food;

    final static Food OUT_OF_RANGE_FOOD = new Food(Integer.MAX_VALUE, Integer.MAX_VALUE, null, new SecureRandom());

    private final int foodCount;

    private final List<World2dObject> objects;


    public World2d(int worldWidth, int worldHeight, int foodCount) {
        rnd = new SecureRandom();

        food = new ArrayList<Food>(foodCount);
        objects = new ArrayList<World2dObject>();
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
            b.x = Math.min(Math.max(b.x, 0), worldWidth - 1);
            b.y = Math.min(Math.max(b.y, 0), worldHeight - 1);

            //eat food
            synchronized (worldLock) {
                b.postRoundTrigger();
            }
        }
        //System.out.println("Food computation took: " + (System.nanoTime() - start));
    }


    public final void randomFood() {
        //FIXME: this seems to interfere with drawing, at least i'm getting an occasional concurrent list modification from within the food painting method
        //TEMP FIX - this might be faster than creating tons of food object every round
        if (food.size() != foodCount) {
            food.clear();
            for (int i = 0; i < foodCount; i++) {
                food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), this, rnd));
            }
        } else {
            for (Food f : food) {
                f.randomPosition();
            }
        }
    }


    public static double foodDist(Food f, int x, int y) {
        return Math.sqrt(((x - f.x) * (x - f.x)) + ((y - f.y) * (y - f.y)));
    }


    public Food findNearestFood(int x, int y) {
        double minDist = 1000000;
        double curDist;
        int indexMinDist = -1;
        for (int i = 0; i < food.size(); i++) {
            curDist = foodDist(food.get(i), x, y);
            //limit visible range to 200
            //if (curDist > 200)
            //continue;
            if (curDist < minDist) {
                minDist = curDist;
                indexMinDist = i;
            }
        }
        if (indexMinDist > -1) {
            return food.get(indexMinDist);
        } else {
            return World2d.OUT_OF_RANGE_FOOD;
        }
    }


    public void draw(Graphics g) {
        if (curOrganisms != null) {
            for (BaseOrganism o : curOrganisms) {
                ((Organism2d) o).draw(g);
            }
        }

        for (Food f : food) {
            f.draw(g);
        }
    }

}
