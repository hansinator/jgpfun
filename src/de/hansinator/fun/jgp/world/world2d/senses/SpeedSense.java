package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.senses.OrientationSense.Gene;

public class SpeedSense implements SensorInput, BodyPart<Body2d>
{
	private final SensorInput[] inputs = { this };

	private final Body2d body;

	public SpeedSense(Body2d body2d)
	{
		body = body2d;
	}

	@Override
	public int get()
	{
		return (int) (body.lastSpeed * Simulator.intScaleFactor);
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
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
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
			return new SpeedSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new SpeedSense(context);
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