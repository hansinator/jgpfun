package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.awt.Point;



public abstract class AnimatableObject
{
	public double dir;

	protected World2d world;
	
	public volatile boolean selected = false;
	
	public AnimatableObject(World2d world, double dir)
	{
		this.dir = dir;
		this.world = world;
	}

	public abstract void draw(Graphics g);
}
