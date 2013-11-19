package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class World2dObject extends Point.Double
{
	final List<CollisionListener> collisionListeners = new ArrayList<CollisionListener>();

	public double dir;

	protected final World2d world;

	public World2dObject(World2d world, double x, double y, double dir)
	{
		super(x, y);
		this.dir = dir;
		this.world = world;
	}

	public static double distance(Point.Double p, double x, double y)
	{
		return Math.sqrt(((x - p.x) * (x - p.x)) + ((y - p.y) * (y - p.y)));
	}

	public static double distance(Point.Double p1, Point.Double p2)
	{
		return Math.sqrt(((p2.x - p1.x) * (p2.x - p1.x)) + ((p2.y - p1.y) * (p2.y - p1.y)));
	}

	final synchronized public boolean addCollisionListener(CollisionListener listener)
	{
		return collisionListeners.add(listener);
	}

	final synchronized public boolean removeCollisionListener(CollisionListener listener)
	{
		return collisionListeners.remove(listener);
	}

	final void collision(World2dObject object)
	{
		for(CollisionListener listener : collisionListeners)
			listener.onCollision(this, object);
	}

	public interface CollisionListener
	{
		public void onCollision(World2dObject a, World2dObject b);
	}

	public abstract void draw(Graphics g);

}
