package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;

import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.DrawablePart;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;

/**
 * Sensory input to locate objects in world. Currently only locates food
 * objects.
 * 
 * @author Hansinator
 * 
 */
public class ObjectLocator implements DrawablePart
{

	private final Body2d body;

	private final World2d world;

	public Food target;

	private double objDist;

	public final SensorInput senseDirX = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (((target.x - body.x) / objDist) * Organism2d.intScaleFactor);
		}

	};

	public final SensorInput senseDirY = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (((target.y - body.y) / objDist) * Organism2d.intScaleFactor);
		}

	};

	public final SensorInput senseDist = new SensorInput()
	{

		@Override
		public int get()
		{
			return (int) (objDist * Organism2d.intScaleFactor);
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

	public ObjectLocator(Body2d body, World2d world)
	{
		this.body = body;
		this.world = world;
	}

	public void locate()
	{
		target = world.findNearestFood(body);
		objDist = World2dObject.distance(target, Math.round((float) body.x), Math.round((float) body.y));
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
	public void prepareInputs()
	{
		locate();
	}

	@Override
	public void processOutputs()
	{
	}

	@Override
	public void draw(Graphics g)
	{
		if (target != null)
		{
			g.setColor(Color.darkGray);
			g.drawLine(Math.round((float) body.x), Math.round((float) body.y), (int) Math.round(target.x),
					(int) Math.round(target.y));
		}
	}
}
