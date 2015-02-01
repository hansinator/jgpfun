package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

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
			return Math.round((float) body.x);
		}
	};

	public final SensorInput senseY = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float) body.y);
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
	
	
	public class Gene implements IOUnit.Gene<Body2d>
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
