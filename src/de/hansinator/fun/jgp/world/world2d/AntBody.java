package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

/**
 * 
 * @author Hansinator
 */
public class AntBody extends Body2d
{
	private static final int foodPickupRadius = Settings.getInt("foodPickupRadius");

	public final ObjectLocator locator;


	public AntBody(Organism<World2d> organism)
	{
		super(organism, 0.0, 0.0, 0.0);

		// init locator sense
		locator = new ObjectLocator(this);
	}

	/*
	 * XXX TODO: put collision logic into objects, like in food. food in turn
	 * adds itself to the fitness count if it encountered an organism. this way
	 * round it's more natural, because the items are the "special" things with
	 * the special logic that changes from scenario to scenario. the organism
	 * itself may stay the same chassis for different scenarios then.
	 * 
	 * also maybe create a line following scenario
	 */

	@Override
	public void postRoundTrigger()
	{
		Food food = locator.target;
		if ((food.x >= (x - foodPickupRadius)) && (food.x <= (x + foodPickupRadius))
				&& (food.y >= (y - foodPickupRadius)) && (food.y <= (y + foodPickupRadius)))
		{
			organism.incFitness();
			food.randomPosition();
		}
	}

	@Override
	public void collision(World2dObject object)
	{
		// TODO Auto-generated method stub

	}
}
