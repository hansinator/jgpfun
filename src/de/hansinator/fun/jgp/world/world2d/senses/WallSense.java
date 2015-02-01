package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author hansinator
 */
public class WallSense implements SensorInput, BodyPart<Body2d>
{

	private double worldWidth, worldHeight;

	private final Body2d body;

	SensorInput[] inputs = { this };

	private int lastSenseVal = 0;

	public WallSense(Body2d body)
	{
		this.body = body;
	}

	@Override
	public int get()
	{
		return lastSenseVal;
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
	}

	@Override
	public void applyOutputs()
	{
		// pickup wallsense in applyOutputs before coordinates are clipped
		double dir = body.dir, temp = 0.0;

		// clip to 2*PI range
		dir = dir - ((double) Math.round(dir / (2 * Math.PI)) * 2 * Math.PI);

		if ((body.x < 0) || (body.x >= worldWidth))
		{
			// TODO: fix abs stuff
			temp = Math.min(Math.abs(2 * Math.PI - dir), Math.abs(Math.PI - dir));
			if ((body.y < 0) || (body.y >= worldHeight))
				temp = Math.min(temp, Math.min(Math.abs(0.5 * Math.PI - dir), Math.abs(1.5 * Math.PI - dir)));
		} else if ((body.y < 0) || (body.y >= worldHeight))
			temp = Math.min(Math.abs(0.5 * Math.PI - dir), Math.abs(1.5 * Math.PI - dir));

		lastSenseVal = (int) Math.round(temp * Simulator.intScaleFactor);
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.worldWidth = Math.floor(context.getWorld().getWidth());
		this.worldHeight = Math.floor(context.getWorld().getHeight());
	}
	
	public static class Gene implements IOUnit.Gene<Body2d>
	{

		@Override
		public void mutate()
		{
		}

		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setMutationChance(int mutationChance)
		{
		}

		@Override
		public int getMutationChance()
		{
			return 0;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new WallSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new WallSense(context);
		}

		@Override
		public int getInputCount()
		{
			return 1;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

	}
}
