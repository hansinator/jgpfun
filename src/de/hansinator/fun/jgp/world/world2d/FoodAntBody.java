package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

/**
 * 
 * @author Hansinator
 */
public class FoodAntBody extends Body2d
{
	private static final int foodPickupRadius = Settings.getInt("foodPickupRadius");

	public final ObjectLocator locator;

	// FIXME: refactor num inputs & outputs bullshit
	public FoodAntBody(Organism2d organism, int numInputs, int numOutputs, boolean useInternalLocator)
	{
		super(organism, 0.0, 0.0, 0.0, new SensorInput[numInputs], new ActorOutput[numOutputs]);

		// init and maybe add locator sense
		locator = new ObjectLocator(this);
		if (useInternalLocator)
			addBodyPart(locator);
	}

	@Override
	public void postRoundTrigger()
	{
		Food food = locator.target;
		if ((food.x >= (x - foodPickupRadius)) && (food.x <= (x + foodPickupRadius))
				&& (food.y >= (y - foodPickupRadius)) && (food.y <= (y + foodPickupRadius)))
		{
			organism.incFood();
			food.randomPosition();
		}
	}

	@Override
	public void collision(World2dObject object)
	{
		// TODO Auto-generated method stub

	}
}
