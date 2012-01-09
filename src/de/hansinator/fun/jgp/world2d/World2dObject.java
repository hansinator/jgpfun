package de.hansinator.fun.jgp.world2d;

import java.awt.Graphics;
import java.awt.Point;

public abstract class World2dObject extends Point.Double {

    protected final World2d world;


    public World2dObject(World2d world, double x, double y) {
        super(x, y);
        this.world = world;
    }


    public static double distance(Point.Double p, double x, double y) {
        return Math.sqrt(((x - p.x) * (x - p.x)) + ((y - p.y) * (y - p.y)));
    }


    public static double distance(Point.Double p1, Point.Double p2) {
        return Math.sqrt(((p2.x - p1.x) * (p2.x - p1.x)) + ((p2.y - p1.y) * (p2.y - p1.y)));
    }


    public abstract void draw(Graphics g);

}
