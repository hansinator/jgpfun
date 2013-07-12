package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.world.world2d.World2dObject.CollisionListener;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

/**
 * 
 * @author Hansinator
 */
public class AntBody extends Body2d implements CollisionListener
{
	public final ObjectLocator locator;


	public AntBody(Organism<World2d> organism)
	{
		super(organism, 0.0, 0.0, 0.0);

		// init locator sense
		locator = new ObjectLocator(this);

		//listen for collsions
		addCollisionListener(this);
	}

	@Override
	public void onCollision(World2dObject a, World2dObject b)
	{
		if (b instanceof Food)
		{
			organism.incFitness();
			((Food) b).randomPosition();
		}
	}
}
