package de.hansinator.fun.jgp.world.world2d;

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

	
	public AntBody(Organism2d organism)
	{
		super(organism, 0.0, 0.0, 0.0);

		// init locator sense
		locator = new ObjectLocator(this);
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
