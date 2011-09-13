package jgpfun.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author hansinator
 */
public class Food extends Point {

    private final World2d world;

    private final Random rnd;


    public Food(int x, int y, World2d world, Random rnd) {
        super(x, y);
        this.world = world;
        this.rnd = rnd;
    }


    public void randomPosition() {
        x = rnd.nextInt(world.worldWidth);
        y = rnd.nextInt(world.worldHeight);
    }


    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, 2, 2);
    }

}
