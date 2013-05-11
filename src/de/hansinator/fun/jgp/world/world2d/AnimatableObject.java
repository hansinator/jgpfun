package de.hansinator.fun.jgp.world.world2d;

public abstract class AnimatableObject extends World2dObject
{
	public AnimatableObject(World2d world, double x, double y, double dir)
	{
		super(world, x, y, dir);
	}

	public abstract void postRoundTrigger();
}
