package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d.DrawablePart;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;

/**
 * 
 * @author Hansinator
 */
public class RadarSense implements SensorInput, ActorOutput, DrawablePart
{

	private final World2dObject origin;

	private World world;

	public double direction = 0.0;

	public static final double beamLength = 200.0;

	public Point2D target = null;

	public final SensorInput senseDirection = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (direction * Organism2d.intScaleFactor);
		}
	};

	SensorInput[] inputs = { this, senseDirection };

	ActorOutput[] outputs = { this };

	public RadarSense(World2dObject origin)
	{
		this.origin = origin;
	}

	public boolean pointInLine(double x1, double y1, double x2, double y2, Point2D p)
	{
		double x3, y3, m, b, y;

		x3 = p.getX();
		y3 = p.getY();
		m = (y2 - y1) / (x2 - x1);
		b = y1 - m * x1;
		y = m * x3 + b;

		// point is near (better, as a longer radar skips pixels at the outer
		// end when moving)
		return Math.abs(y - y3) < 3.0;

		// real match
		// return Math.round(y) == Math.round(y3);
	}

	@Override
	public int get()
	{
		double x1, y1, x2, y2, rdir, bdir;

		// line start
		x1 = Math.floor(origin.x);
		y1 = Math.floor(origin.y);

		// line end
		rdir = direction - ((double) Math.round(direction / (2 * Math.PI)) * 2 * Math.PI);
		bdir = origin.dir - ((double) Math.round(origin.dir / (2 * Math.PI)) * 2 * Math.PI);
		x2 = Math.floor(origin.x + beamLength * Math.sin(rdir + bdir));
		y2 = Math.floor(origin.y - beamLength * Math.cos(rdir + bdir));

		for (Food f : ((World2d)world).food)
			if (pointInLine(x1, y1, x2, y2, f)
					&& (Math.sqrt(((x1 - f.x) * (x1 - f.x)) + ((y1 - f.y) * (y1 - f.y))) <= beamLength)
					&& (Math.abs(x1 + 2.0 * Math.sin(rdir + bdir) - f.x) < Math.abs(x1 - f.x)))
			{
				target = f;
				return Integer.MAX_VALUE;
			}
		target = null;
		return 0;
	}

	@Override
	public void set(int value)
	{
		direction += (Math.max(-65535, Math.min(value, 65535)) / Organism2d.intScaleFactor) / 10.0;
		direction -= 2 * Math.PI
				* (direction < 0.0 ? Math.ceil(direction / (2 * Math.PI)) : (Math.floor(direction / (2 * Math.PI))));
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return outputs;
	}

	@Override
	public void sampleInputs()
	{
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void draw(Graphics g)
	{
		final int x_center = Math.round((float) origin.x);
		final int y_center = Math.round((float) origin.y);

		if (target == null)
		{
			g.setColor(Color.darkGray);
			double rdir, bdir;
			rdir = direction - ((double) Math.round(direction / (2 * Math.PI)) * 2 * Math.PI);
			bdir = origin.dir - ((double) Math.round(origin.dir / (2 * Math.PI)) * 2 * Math.PI);
			g.drawLine(x_center, y_center,
					Math.round((float) (origin.x + RadarSense.beamLength * Math.sin(rdir + bdir))),
					Math.round((float) (origin.y - RadarSense.beamLength * Math.cos(rdir + bdir))));
		} else if (target != null)
		{
			g.setColor(Color.blue);
			g.drawLine(x_center, y_center, (int) Math.round(target.getX()), (int) Math.round(target.getY()));
		}

	}

	@Override
	public void addToWorld(World world)
	{
		this.world = world;
	}
}
