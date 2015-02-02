package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;

/**
 * 
 * @author Hansinator
 */
public class RadarSense implements SensorInput, ActorOutput, BodyPart.DrawablePart<Body2d>
{
	private final static double sweepSpeedScaleFactor = Settings.getDouble("radarSweepSpeedScaleFactor");
	
	private final World2dObject origin;

	private World2d world;

	public double direction = 0.0;

	public static final double beamLength = 200.0;

	public Point2D target = null;
	
	public static Color beamColor = new Color(24, 24, 24);
	
	private double oldBeamX, oldBeamY;

	public final SensorInput senseDirection = new SensorInput() {

		@Override
		public int get()
		{
			return (int) (direction * Simulator.intScaleFactor);
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
	
	// see http://stackoverflow.com/questions/13300904/determine-whether-point-lies-inside-triangle
	public boolean pointInTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Point2D p)
	{
		double alpha = ((y2 - y3) * (p.getX() - x3) + (x3 - x2) * (p.getY() - y3)) /
				(double)((y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3));
		double beta = ((y3 - y1) * (p.getX() - x3) + (x1 - x3) * (p.getY() - y3)) /
				(double)((y2 - y3) * (x1 - x3) + (x3 - x2) * (x1 - y3));
		double gamma = 1.0f - alpha - beta;

		return ((alpha > 0.0) && (beta > 0.0) && (gamma > 0.0)) ||
				((alpha == 1.0) && (beta == 0.0) && (gamma == 0.0)) ||
				((alpha == 0.0) && (beta == 1.0) && (gamma == 0.0)) ||
				((alpha == 0.0) && (beta == 0.0) && (gamma == 1.0));
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
		
		for (Food f : world.food)
		{
			boolean hit = false;
			// see if this is a triangle or a line
			if(!(oldBeamX == x2) && (oldBeamY == y2)) hit = pointInTriangle(x1, y1, x2, y2, oldBeamX, oldBeamY, f);
			else hit = pointInLine(x1, y1, x2, y2, f)
					// test if distance to point is within beamLength
					&& (Math.sqrt(((x1 - f.x) * (x1 - f.x)) + ((y1 - f.y) * (y1 - f.y))) <= beamLength)
					&& (Math.abs(x1 + 2.0 * Math.sin(rdir + bdir) - f.x) < Math.abs(x1 - f.x));
			if(hit)
			{
				target = f;
				oldBeamX = x2;
				oldBeamY = y2;
				return Integer.MAX_VALUE;
			}
		}
		target = null;
		
		oldBeamX = x2;
		oldBeamY = y2;
		
		return 0;
	}

	@Override
	public void set(int value)
	{
		direction += ((double)value / (double)Integer.MAX_VALUE) / sweepSpeedScaleFactor;
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
			g.setColor(beamColor);
			double rdir, bdir, x, y;
			rdir = direction - ((double) Math.round(direction / (2 * Math.PI)) * 2 * Math.PI);
			bdir = origin.dir - ((double) Math.round(origin.dir / (2 * Math.PI)) * 2 * Math.PI);
			x = origin.x + RadarSense.beamLength * Math.sin(rdir + bdir);
			y = origin.y - RadarSense.beamLength * Math.cos(rdir + bdir);
			g.fillPolygon(new int[]{x_center, Math.round((float)x), Math.round((float)oldBeamX)}, new int[]{y_center, Math.round((float)y), Math.round((float)oldBeamY)} , 3);
		} else if (target != null)
		{
			g.setColor(Color.blue);
			g.drawLine(x_center, y_center, (int) Math.round(target.getX()), (int) Math.round(target.getY()));
		}

	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.world = context.getWorld();
	}

	public static class Gene extends IOUnit.Gene<Body2d>
	{
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new RadarSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new RadarSense(context);
		}

		@Override
		public int getInputCount()
		{
			return 2;
		}

		@Override
		public int getOutputCount()
		{
			return 1;
		}

	}
}
