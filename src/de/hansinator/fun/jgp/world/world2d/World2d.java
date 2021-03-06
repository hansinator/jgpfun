package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.gui.ExecutionUnitGeneView;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public class World2d implements World, ContactListener
{

	public final static Object FOOD_TAG = new Object();

	public final static Object EATEN_TAG = new Object();

	private final Random rnd;

	public final int worldWidth, worldHeight;

	public final List<Body> food;

	final static Vec2 OUT_OF_RANGE_FOOD = new Vec2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

	private final int foodCount;

	private final List<Body2d> bodies;

	private org.jbox2d.dynamics.World world;

	public org.jbox2d.dynamics.World getWorld()
	{
		return world;
	}

	private Body groundBody;

	de.hansinator.fun.jgp.gui.DebugDrawJ2D draw;

	public de.hansinator.fun.jgp.gui.DebugDrawJ2D getDraw()
	{
		return draw;
	}

	public void setDraw(de.hansinator.fun.jgp.gui.DebugDrawJ2D draw)
	{
		this.draw = draw;
		world.setDebugDraw(draw);
		setCamera(new Vec2(worldWidth / 2, worldHeight / 2), -2);
	}

	public World2d(int worldWidth, int worldHeight, int foodCount)
	{
		rnd = Settings.newRandomSource();

		food = new ArrayList<Body>(foodCount);
		bodies = new ArrayList<Body2d>();
		resetState();

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.foodCount = foodCount;
	}

	public void setCamera(Vec2 argPos, float scale)
	{
		draw.setCamera(argPos.x, argPos.y, scale);
	}

	@Override
	public void animate()
	{
		int flags = 0;
		flags += DebugDraw.e_shapeBit;
		flags += DebugDraw.e_jointBit;
		// flags += DebugDraw.e_aabbBit;
		// flags += DebugDraw.e_centerOfMassBit;
		// flags += DebugDraw.e_dynamicTreeBit;
		draw.setFlags(flags);

		// physics, baby!
		float hz = 60;
		float timeStep = hz > 0f ? 1f / hz : 0;
		world.step(timeStep, 8, 3);

		for (int i = 0; i < food.size(); i++)
		{
			Body b = food.get(i);
			if (b.getUserData() == World2d.EATEN_TAG)
			{
				b.setUserData(FOOD_TAG);
				b.setTransform(new Vec2((float) rnd.nextInt(worldWidth), (float) rnd.nextInt(worldHeight)), 0f);
			}
		}
	}

	@Override
	public final void resetState()
	{
		bodies.clear();

		Vec2 gravity = new Vec2(0, 0);
		world = new org.jbox2d.dynamics.World(gravity);

		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);

		world.setAllowSleep(true);
		// world.setSubStepping(true);
		world.setContinuousPhysics(true);
		// world.setDestructionListener(destructionListener);
		world.setContactListener(this);
		world.setDebugDraw(draw);

		{
			final float k_restitution = 0.4f;

			EdgeShape shape = new EdgeShape();

			FixtureDef sd = new FixtureDef();
			sd.shape = shape;
			sd.density = 0.0f;
			sd.restitution = k_restitution;

			// left wall
			shape.set(new Vec2(worldWidth, 0), new Vec2(worldWidth, worldHeight - 1));
			groundBody.createFixture(sd);

			// right wall
			shape.set(new Vec2(1.0f, 0), new Vec2(1.0f, worldHeight - 1));
			groundBody.createFixture(sd);

			// top wall
			shape.set(new Vec2(1.0f, 0.0f), new Vec2(worldWidth, 0.0f));
			groundBody.createFixture(sd);

			// bottom wall
			shape.set(new Vec2(1.0f, worldHeight - 1), new Vec2(worldWidth, worldHeight - 1));
			groundBody.createFixture(sd);
		}

		// create food
		{
			CircleShape circle = new CircleShape();
			circle.m_radius = 1.6f;
			FixtureDef fd = new FixtureDef();
			fd.shape = circle;
			fd.isSensor = true;
			BodyDef bd = new BodyDef();

			food.clear();
			for (int i = 0; i < foodCount; i++)
			{
				bd.position.set((float) rnd.nextInt(worldWidth), (float) rnd.nextInt(worldHeight));
				Body f = world.createBody(bd);
				f.createFixture(fd);
				f.setUserData(FOOD_TAG);
				food.add(f);
			}
		}
	}

	public Vec2 findNearestFood(Vec2 p)
	{
		double minDist = 1000000;
		double curDist;
		int indexMinDist = -1;
		for (int i = 0; i < food.size(); i++)
		{
			Vec2 f = food.get(i).getPosition();
			curDist = Math.sqrt(((p.x - f.x) * (p.x - f.x)) + ((p.y - f.y) * (p.y - f.y)));

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
			return food.get(indexMinDist).getPosition();
		else
			return OUT_OF_RANGE_FOOD;
	}

	@Override
	public void clickEvent(MouseEvent e, Map<ExecutionUnit<? extends World>, Genome> generation)
	{
		Vec2 clickPos = new Vec2();
		
		{
			Point p = e.getPoint();
			draw.getViewportTranform().getScreenToWorld(new Vec2(p.x, p.y), clickPos);
		}

		// TODO probably create a selectable interface in the future, but for
		// now coupling of world2d and other 2d objects is ok
		// see if we hit an object
		for (Body2d b : bodies)
		{
			Vec2 p = b.getBody().getPosition();
			if (Math.abs(p.x - clickPos.x) < 10.0 && Math.abs(p.y - clickPos.y) < 10.0)
			{
				// TODO add an object inspector view that shows info about the
				// selected object
				if (e.getClickCount() == 1)
					b.selected = true;
				else if (e.getClickCount() == 2)
				{
					// show a gene viewer
					Genome genome = generation.get(b.parent);
					if (genome != null)
					{
						ExecutionUnitGeneView view = genome.getRootGene().getView();
						if (view != null)
							view.show();
					}
				}
			} else if (e.getClickCount() == 1)
				b.selected = false;
		}
	}

	private final Vec2 screenPos = new Vec2();
	@Override
	public void draw(Graphics g, Map<ExecutionUnit<? extends World>, Genome> generation)
	{
		world.drawDebugData();

		// TODO find a better solution like a separate BodyView / ObjectView
		// class
		// draw fitness onto bodies
		for (Body2d b : bodies)
		{
			b.draw(g);
			
			Genome genome = generation.get(b.parent);
			draw.getViewportTranform().getWorldToScreen(b.getBody().getPosition(), screenPos);

			if (genome != null)
			{
				g.setColor(Color.green);
				g.drawString("" + genome.getFitnessEvaluator().getFitness(), Math.round(screenPos.x + 6),
						Math.round(screenPos.y - 8));
			}
			
			// FIXME hacky implementation here
			if (b.selected)
			{
				g.setColor(Color.cyan);
				g.drawOval(Math.round(screenPos.x), Math.round(screenPos.y), 5, 5);
			}
		}
	}

	public synchronized void registerObject(Body2d object)
	{
		bodies.add(object);
	}

	public synchronized void unregisterObject(Body2d object)
	{
		bodies.remove(object);
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
	public void beginContact(Contact contact)
	{
		Object o = contact.m_fixtureA.m_body.getUserData();

		// execute collisions
		if (o instanceof Body2d)
		{
			((Body2d) o).collision(contact.m_fixtureB.m_body);
		}
	}

	@Override
	public void endContact(Contact contact)
	{

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{

	}
}
