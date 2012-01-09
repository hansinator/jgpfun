package de.hansinator.fun.jgp.world2d;

import java.awt.Graphics;
import java.awt.Point;

abstract class World2dObject extends Point {

    protected final World2d world;


    public World2dObject(World2d world, int x, int y) {
        super(x, y);
        this.world = world;
    }

    public abstract void draw(Graphics g);
}
