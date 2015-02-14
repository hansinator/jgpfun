package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.testbed.framework.TestbedSettings;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.gui.ExecutionUnitGeneView;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
/*
 * TODO decouple draw and click stuff completely out of this class into a worldview
 * TODO this class should only hold its data and offer methods to change them in various ways (such as animate)
 * TODO as a data class it should expose enough getters and setters to let a worldview display all of it
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
	
	private org.jbox2d.dynamics.World world;
	
	public org.jbox2d.dynamics.World getWorld()
	{
		return world;
	}

	private Body groundBody;

	de.hansinator.fun.jgp.gui.DebugDrawJ2D draw;

	public void setDraw(de.hansinator.fun.jgp.gui.DebugDrawJ2D draw)
	{
		this.draw = draw;
		world.setDebugDraw(draw);
		setCamera(new Vec2(1024, 768), -1);
	}

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
	
	public void setCamera(Vec2 argPos, float scale) {
	    draw.setCamera(argPos.x, argPos.y, scale);
	  }

	@Override
	public void animate()
	{
		/*
			m_world.setAllowSleep(settings.getSetting(TestbedSettings.AllowSleep).enabled);
		    m_world.setWarmStarting(settings.getSetting(TestbedSettings.WarmStarting).enabled);
		    m_world.setSubStepping(settings.getSetting(TestbedSettings.SubStepping).enabled);
		    m_world.setContinuousPhysics(settings.getSetting(TestbedSettings.ContinuousCollision).enabled);
		 */
		int flags = 0;
	    flags += DebugDraw.e_shapeBit;
	    flags += DebugDraw.e_jointBit;
	    //flags += DebugDraw.e_aabbBit;
	    //flags += DebugDraw.e_centerOfMassBit;
	    //flags += DebugDraw.e_dynamicTreeBit;
	    draw.setFlags(flags);
	    
		// physics, baby!
		float hz = 60;
	    float timeStep = hz > 0f ? 1f / hz : 0;
		world.step(timeStep, 8, 3);
		
		
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
		
		Vec2 gravity = new Vec2(0, 0);
	    world = new org.jbox2d.dynamics.World(gravity);
	    
	    BodyDef bodyDef = new BodyDef();
	    groundBody = world.createBody(bodyDef);
	    
	    //world.setDestructionListener(destructionListener);
	    //world.setContactListener(this);
	    world.setDebugDraw(draw);
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
		
		// TODO probably create a selectable interface in the future, but for now coupling of world2d and other 2d objects is ok
		// see if we hit an object
		for (World2dObject o : objects)
			if (Math.abs(o.x - p.x) < 10.0 && Math.abs(o.y - p.y) < 10.0)
			{
				// TODO add an object inspector view that shows info about the selected object
				if(e.getClickCount() == 1)
					o.selected = true;
				else if((e.getClickCount() == 2) && (o instanceof Body2d))
				{
					// show a gene viewer
					Genome genome = generation.get(((Body2d)o).parent);
					if(genome != null)
					{
						ExecutionUnitGeneView view = genome.getRootGene().getView();
						if(view != null)
							view.show();
					}
				}
			}
			else if(e.getClickCount() == 1) o.selected = false;
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

	@Override
	public void debugDraw()
	{
		world.drawDebugData();
	}
}
