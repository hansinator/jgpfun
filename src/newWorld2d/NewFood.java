package newWorld2d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * 
 * @author hansinator
 */
public class NewFood extends NewWorld2dObject
{

	private final Random rnd;

	public NewFood(double x, double y, NewWorld2d world, Random rnd)
	{
		super(world, x, y, 0.0);
		this.rnd = rnd;
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
