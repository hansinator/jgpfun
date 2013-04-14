package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

/**
 * 
 * @author Hansinator
 */
public class NewFoodAntBody extends Body2d
{
	public final ObjectLocator locator;

	// FIXME: refactor num inputs & outputs bullshit
	public NewFoodAntBody(Organism2d organism, World2d world, int numInputs, int numOutputs, boolean useInternalLocator)
	{
		super(organism, 0.0, 0.0, 0.0, new SensorInput[numInputs], new ActorOutput[numOutputs]);

		// init and maybe add locator sense
		locator = new ObjectLocator(world, this);
		if (useInternalLocator)
			addBodyPart(locator);
	}

	@Override
	public void postRoundTrigger()
	{

	}

	@Override
	public void collision(World2dObject object)
	{
		if (object instanceof Food)
		{
			organism.incFood();
			((Food) object).randomPosition();
		}
	}
}
