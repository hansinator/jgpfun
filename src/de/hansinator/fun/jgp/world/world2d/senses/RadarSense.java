package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author Hansinator
 */
public class RadarSense implements SensorInput, ActorOutput, BodyPart.DrawablePart<Body2d>, RayCastCallback
{
	private final static double sweepSpeedScaleFactor = Settings.getDouble("radarSweepSpeedScaleFactor");

	private org.jbox2d.dynamics.Body origin;

	private World2d world;

	public double direction = 0.0;

	public static final double beamLength = Settings.getDouble("radarBeamLength");

	private Vec2 target = new Vec2();

	private boolean hit = false;

	public static Color beamColor = new Color(24, 24, 24);

	private Vec2 beamPos = new Vec2();

	private Vec2 oldBeamPos = new Vec2();

	public final SensorInput senseDirection = new SensorInput() {

		@Override
		public double get()
		{
			return direction * EvolutionaryProcess.intScaleFactor;
		}
	};

	SensorInput[] inputs = { this, senseDirection };

	ActorOutput[] outputs = { this };

	@Override
	public double get()
	{
		double rdir, bdir;
		float angle = origin.getAngle();
		Vec2 pos = origin.getPosition();

		// compute new beam end position
		rdir = direction - ((double) Math.round(direction / (2 * Math.PI)) * 2 * Math.PI);
		bdir = angle - ((double) Math.round(angle / (2 * Math.PI)) * 2 * Math.PI);
		oldBeamPos.set(beamPos);
		beamPos.set(Math.round(pos.x + beamLength * Math.sin(rdir + bdir)),
				Math.round(pos.y - beamLength * Math.cos(rdir + bdir)));

		// trace the beam path using bresenham's line algorithm and query each
		// intermediate beam pos for any collisions
		hit = false;
		rayCastAlongLine(oldBeamPos, beamPos, 1.0f);

		// if we hit something compute and return distance
		if (hit)
		{
			Vec2 o = origin.getPosition();
			float curDist = (float) Math.sqrt(((target.x - o.x) * (target.x - o.x))
					+ ((target.y - o.y) * (target.y - o.y)));
			return Math.round((Integer.MAX_VALUE / beamLength) * curDist);
		}

		return 0.0;
	}

	private final Vec2 testPoint = new Vec2();
	private void rayCastAlongLine(final Vec2 start, final Vec2 end, final float interval)
	{
		float dx1 = 0.0f, dy1 = 0.0f, dx2 = 0.0f, dy2 = 0.0f;
		
		// compute delta
		testPoint.set(end);
		testPoint.subLocal(start);
	
		// choose proper step directions
		dx1 = dx2 = Math.signum(testPoint.x) * interval;
		dy1 = Math.signum(testPoint.y) * interval;

		// compute loop values
		int longest = Math.abs(Math.round(testPoint.x));
		int shortest = Math.abs(Math.round(testPoint.y));
		if (!(longest > shortest))
		{
			// swap values
			int x = longest;
			longest = shortest;
			shortest = x;
			
			// choose proper step directions
			dy2 = Math.signum(testPoint.y) * interval;
			dx2 = 0.0f;
		}

		// finally "draw" the line
		testPoint.set(start);
		for (int i = 0, numerator = longest >> 1; i <= longest; i++)
		{
			synchronized (world) // FIXME hacky locking object
			{
				world.getWorld().raycast(this, origin.getPosition(), testPoint);
			}
			numerator += shortest;
			if (!(numerator < longest))
			{
				numerator -= longest;
				testPoint.addLocal(dx1, dy1);
			} else
			{
				testPoint.addLocal(dx2, dy2);
			}
		}
	}

	@Override
	public void set(int value)
	{
		direction += ((double) value / (double) Integer.MAX_VALUE) / sweepSpeedScaleFactor;
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

	private final Vec2 o = new Vec2();
	private final Vec2 t1 = new Vec2();
	private final Vec2 t2 = new Vec2();

	@Override
	public void draw(Graphics g)
	{
		if (origin == null)
			return;

		world.getDraw().getViewportTranform().getWorldToScreen(origin.getPosition(), o);
		int x = Math.round(o.x);
		int y = Math.round(o.y);

		if (!hit)
		{
			world.getDraw().getViewportTranform().getWorldToScreen(beamPos, t1);
			world.getDraw().getViewportTranform().getWorldToScreen(oldBeamPos, t2);

			g.setColor(beamColor);
			g.fillPolygon(new int[] { x, Math.round(t1.x), Math.round(t2.x) },
					new int[] { y, Math.round(t1.y), Math.round(t2.y) }, 3);
		} else
		{
			world.getDraw().getViewportTranform().getWorldToScreen(target, t1);
			g.setColor(Color.blue);
			g.drawLine(x, y, Math.round(t1.x), Math.round(t1.y));
		}

	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.world = context.getWorld();
		this.origin = context.getBody();

		// init oldBeamPos
		get();
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
			return new RadarSense();
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

	@Override
	public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction)
	{
		target.set(point);
		hit = true;

		// clip the ray to the closest hit
		return 1;
	}
}
