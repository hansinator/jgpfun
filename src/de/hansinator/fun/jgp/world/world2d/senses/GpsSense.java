package de.hansinator.fun.jgp.world.world2d.senses;

import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.Part;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;

/**
 * 
 * @author hansinator
 */
public class GpsSense implements Part
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
	public void addToWorld(World world)
	{
		// TODO Auto-generated method stub
		
	}
}
