package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * 
 * @author hansinator
 */
public class Food extends World2dObject
{

	private final Random rnd;

	public Food(double x, double y, World2d world, Random rnd)
	{
		super(world, x, y, 0.0);
		this.rnd = rnd;
		if(world != null)
			world.registerObject(this);
	}

	public void randomPosition()
	{
		x = rnd.nextInt(world.getWidth());
		y = rnd.nextInt(world.getHeight());
	}

	@Override
	public void draw(Graphics g)
	{
		g.setColor(Color.green);
		g.fillOval((int) Math.round(x - 1), (int) Math.round(y - 1), 3, 3);
	}

}
