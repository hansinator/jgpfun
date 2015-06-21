package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

public class OrientationSense implements SensorInput, BodyPart<Body2d>
{
	private final SensorInput[] inputs = { this };

	private Body body;
	
	private float angle;

	@Override
	public int get()
	{
		// could also be sin
		return (int) (Math.cos(angle) * EvolutionaryProcess.intScaleFactor);
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
		angle = body.getAngle();
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.body = context.getBody();
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
			return new OrientationSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new OrientationSense();
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