package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public class World2d implements World
{

	private final Random rnd;

	public final int worldWidth, worldHeight;

	public final List<Food> food;

	final static Food OUT_OF_RANGE_FOOD = new Food(Integer.MAX_VALUE, Integer.MAX_VALUE, null, Settings.newRandomSource());

	private final int foodCount;

	private final List<World2dObject> objects;

	private final List<AnimatableObject> animatableObjects;

	public World2d(int worldWidth, int worldHeight, int foodCount)
	{
		rnd = Settings.newRandomSource();

		food = new ArrayList<Food>(foodCount);
		objects = new ArrayList<World2dObject>();
		animatableObjects = new ArrayList<AnimatableObject>();
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
		for(AnimatableObject o : animatableObjects)
		{
			// prevent world wrapping
			o.x = Math.min(Math.max(o.x, 0), worldWidth - 1);
			o.y = Math.min(Math.max(o.y, 0), worldHeight - 1);

			// eat food
			// synchronized (worldLock) {
			o.postRoundTrigger();
			// }
		}
	}

	@Override
	public final void resetState()
	{
		if (food.size() != foodCount)
		{
			food.clear();
			for (int i = 0; i < foodCount; i++)
				food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), this, rnd));
		} else for (Food f : food)
			f.randomPosition();

		objects.clear();
		animatableObjects.clear();
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
		else return OUT_OF_RANGE_FOOD;
	}

	@Override
	public void clickEvent(int x, int y)
	{
		// see if we hit an object
		//XXX create a clickable interface
		for (World2dObject o : objects)
			if (Math.abs(o.x - x) < 10.0 && Math.abs(o.y - y) < 10.0)
			{
				// tag it
				//XXX clickable interface!
				//b.tagged = true;
				return;
			}
	}

	@Override
	public void draw(Graphics g)
	{
		for (World2dObject o : objects)
			o.draw(g);

		for (Food f : food)
			f.draw(g);
	}

	@Override
	public void setOrganisms(List<BaseOrganism> organisms)
	{
		// take new organisms and inform them about being here
		for (BaseOrganism<World2d> organism : organisms)
			organism.addToWorld(this);
	}

	public void addObject(World2dObject object)
	{
		objects.add(object);
		if(object instanceof AnimatableObject)
			animatableObjects.add((AnimatableObject)object);
	}

	public int getWidth()
	{
		return worldWidth;
	}

	public int getHeight()
	{
		return worldHeight;
	}
}
