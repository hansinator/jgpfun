package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

/**
 * 
 * @author hansinator
 */
public class GpsSense implements BodyPart<Body2d>
{

	private final Body2d body;

	public final SensorInput senseX = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float)(body.x * Simulator.intScaleFactor));
		}
	};

	public final SensorInput senseY = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float)(body.y * Simulator.intScaleFactor));
		}
	};

	SensorInput[] inputs = { senseX, senseY };

	public GpsSense(Body2d body)
	{
		this.body = body;
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
	public void attachEvaluationState(Body2d body)
	{
		// TODO Auto-generated method stub

	}
	
	
	public class Gene extends IOUnit.Gene<Body2d>
	{
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new GpsSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new GpsSense(context);
		}

		@Override
		public int getInputCount()
		{
			return 2;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

	}
}
