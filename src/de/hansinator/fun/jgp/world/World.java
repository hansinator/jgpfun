package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

import de.hansinator.fun.jgp.world.world2d.Organism2d;

public interface World {
	public void draw(Graphics g);
	public void clickEvent(int x, int y);
	public void animate();
}
