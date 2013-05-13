package de.hansinator.fun.jgp.world.newWorld2d;

import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

/**
 * 
 * @author Hansinator
 */
public class NewAntBody extends Body2d
{
	public final ObjectLocator locator;

	// FIXME: refactor num inputs & outputs bullshit
	public NewAntBody(Organism<World2d> organism)
	{
		super(organism, 0.0, 0.0, 0.0);

		// init and maybe add locator sense
		locator = new ObjectLocator(this);
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
			organism.incFitness();
			((Food) object).randomPosition();
		}
	}
}
