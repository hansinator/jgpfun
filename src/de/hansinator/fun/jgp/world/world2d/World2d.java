package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.life.ExecutionUnit;
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
	public void animate()
	{
		// TODO: have a more compex world, add a barrier in the middle of the
		// screen
		// TODO: take into account ant size, so it can't hide outside of the
		// screen
		for(AnimatableObject ao : animatableObjects)
		{
			// prevent world wrapping
			ao.x = Math.min(Math.max(ao.x, 0), worldWidth - 1);
			ao.y = Math.min(Math.max(ao.y, 0), worldHeight - 1);

			//execute collisions
			int r = ao.getCollisionRadius();
			for(World2dObject o : objects)
				if ((o.x >= (ao.x - r)) && (o.x <= (ao.x + r))
						&& (o.y >= (ao.y - r)) && (o.y <= (ao.y + r)))
					ao.collision(o);
		}
	}

	@Override
	public final void resetState()
	{
		objects.clear();
		animatableObjects.clear();

		if (food.size() != foodCount)
		{
			food.clear();
			for (int i = 0; i < foodCount; i++)
				food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), this, rnd));
		} else for (Food f : food)
		{
			f.randomPosition();
			registerObject(f);
		}
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
	public void clickEvent(MouseEvent e, Map<ExecutionUnit<? extends World>, Genome> generation)
	{
		Point p = e.getPoint();
		
		// see if we hit an object
		//XXX create a clickable interface
		for (World2dObject o : objects)
			if (Math.abs(o.x - p.x) < 10.0 && Math.abs(o.y - p.y) < 10.0)
			{
				// tag it
				//XXX clickable interface!
				//b.tagged = true;
				return;
			}
	}

	@Override
	public void draw(Graphics g, Map<ExecutionUnit<? extends World>, Genome> generation)
	{
		for (World2dObject o : objects)
		{
			o.draw(g);
			
			// TODO find a better solution like a separate BodyView / ObjectView class
			// draw fitness onto bodies
			if(o instanceof Body2d)
			{
				Body2d b = (Body2d)o;
				Genome genome = generation.get(b.parent);
				
				if(genome != null)
				{
					g.setColor(Color.green);
					g.drawString("" + genome.getFitnessEvaluator().getFitness(), Math.round((float) b.x) + 8, Math.round((float) b.y) + 8);
				}
			}
		}

		for (Food f : food)
			f.draw(g);
	}


	public synchronized void registerObject(World2dObject object)
	{
		objects.add(object);
		if(object instanceof AnimatableObject)
			animatableObjects.add((AnimatableObject)object);
	}

	public synchronized void unregisterObject(World2dObject object)
	{
		objects.remove(object);
		if(object instanceof AnimatableObject)
			animatableObjects.remove(object);
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
