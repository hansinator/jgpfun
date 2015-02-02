package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;

/**
 * Sensory input to locate objects in world. Currently only locates food
 * objects.
 * 
 * @author Hansinator
 * 
 */
public class ObjectLocator implements BodyPart.DrawablePart<Body2d>
{

	private World2d world;

	private final World2dObject origin;

	public Food target;

	private double objDist;

	public final SensorInput senseDirX = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (((target.x - origin.x) / objDist) * Simulator.intScaleFactor);
		}

	};

	public final SensorInput senseDirY = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (((target.y - origin.y) / objDist) * Simulator.intScaleFactor);
		}

	};

	public final SensorInput senseDist = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (objDist * Simulator.intScaleFactor);
		}

	};

	public final SensorInput senseDist2 = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float) objDist);
		}

	};

	SensorInput[] inputs = { senseDirX, senseDirY, senseDist, senseDist2 }; // senseDist
	// or
	// senseDist
	// or
	// both2
	// fix??

	public ObjectLocator(World2dObject origin)
	{
		this.origin = origin;
	}

	public void locate()
	{
		target = world.findNearestFood(origin);
		objDist = World2dObject.distance(target, Math.round((float) origin.x), Math.round((float) origin.y));
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return ActorOutput.emptyActorOutputArray;
	}

	@Override
	public void sampleInputs()
	{
		locate();
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void draw(Graphics g)
	{
		if (target != null)
		{
			g.setColor(Color.darkGray);
			g.drawLine(Math.round((float) origin.x), Math.round((float) origin.y), (int) Math.round(target.x),
					(int) Math.round(target.y));
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
			return new ObjectLocator.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new ObjectLocator(context);
		}

		@Override
		public int getInputCount()
		{
			return 4;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

	}
}
