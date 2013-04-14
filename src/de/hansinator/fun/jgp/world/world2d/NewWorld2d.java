package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.awt.Point;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;

public class NewWorld2d implements World
{

	private static final int collisionDistance = Settings.getInt("foodPickupRadius");

	private final Random rnd;

	public final int worldWidth, worldHeight;

	public List<BaseOrganism> curOrganisms;

	public final List<Food> food;

	final static Food OUT_OF_RANGE_FOOD = new Food(Integer.MAX_VALUE, Integer.MAX_VALUE, null,
			Settings.newRandomSource());

	private final int foodCount;

	//fixed objects
	private final List<World2dObject> objects;
	
	//moving objects
	private final List<Body2d> bodies;

	public NewWorld2d(int worldWidth, int worldHeight, int foodCount)
	{
		rnd = new SecureRandom();

		food = new ArrayList<Food>(foodCount);
		objects = new ArrayList<World2dObject>();
		bodies = new ArrayList<Body2d>();
		resetState();

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.foodCount = foodCount;
	}

	@Override
	public void animate(/* Object worldLock */)
	{
		// TODO: have a more compex world, add a barrier in the middle of the
		// screen
		// TODO: take into account ant size, so it can't hide outside of the
		// screen
		for (final Body2d b : bodies)
		{
			// prevent world wrapping
			b.x = Math.min(Math.max(b.x, 0), worldWidth - 1);
			b.y = Math.min(Math.max(b.y, 0), worldHeight - 1);

			// see if there is any collision
			World2dObject wo = findNearestObject(b);
			if ((wo.x >= (b.x - collisionDistance)) && (wo.x <= (b.x + collisionDistance))
					&& (wo.y >= (b.y - collisionDistance)) && (wo.y <= (b.y + collisionDistance)))
			{
				b.collision(wo);
			}
		}
	}

	@Override
	public final void resetState()
	{
		bodies.clear();
		if (food.size() != foodCount)
		{
			food.clear();
			for (int i = 0; i < foodCount; i++)
				food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), this, rnd));
		} else for (Food f : food)
			f.randomPosition();
	}
	
	
	public World2dObject findNearestObject(Point.Double p)
	{
		double minDist = 1000000;
		double curDist;
		int indexMinDist = -1;
		for (int i = 0; i < objects.size(); i++)
		{
			curDist = World2dObject.distance(objects.get(i), p);
			if (curDist < minDist)
			{
				minDist = curDist;
				indexMinDist = i;
			}
		}
		if (indexMinDist > -1)
			return objects.get(indexMinDist);
		else return null;
	}

	public Food findNearestFood(Point.Double p)
	{
		double minDist = 1000000;
		double curDist;
		int indexMinDist = -1;
		for (int i = 0; i < food.size(); i++)
		{
			curDist = World2dObject.distance(food.get(i), p);
			// limit visible range to 200
			// if (curDist > 200)
			// continue;
			if (curDist < minDist)
			{
				minDist = curDist;
				indexMinDist = i;
			}
		}
		if (indexMinDist > -1)
			return food.get(indexMinDist);
		else return World2d.OUT_OF_RANGE_FOOD;
	}

	@Override
	public void clickEvent(int x, int y)
	{
		// see if we hit a body
		for (Body2d b : bodies)
			if (Math.abs(b.x - x) < 10.0 && Math.abs(b.y - y) < 10.0)
			{
				// tag it
				b.tagged = true;
				return;
			}
	}

	@Override
	public void draw(Graphics g)
	{
		for (Body2d b : bodies)
			b.draw(g);

		for (Food f : food)
			f.draw(g);
	}

	@Override
	public void setOrganisms(List<BaseOrganism> organisms)
	{
		// take new organisms and inform them about being here
		for (BaseOrganism organism : curOrganisms)
		{
			((Organism2d) organism).addToWorld(this);
			for(Body2d b : ((Organism2d)organism).bodies)
				bodies.add(b);
		}
	}

	@Override
	public int getWidth()
	{
		return worldWidth;
	}

	@Override
	public int getHeight()
	{
		return worldHeight;
	}
}
