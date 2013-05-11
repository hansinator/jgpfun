package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

/*
 * TODO: refactor this into a generic organismpart that resides in life package
 * and make an abstract BodyPart in this package that implements the generic iface
 * but unifies the duplicate code in clients of this current interface
 */
public interface BodyPart<E extends World>
{
	public interface DrawablePart<E extends World> extends BodyPart<E>
	{
		@SuppressWarnings("rawtypes")
		public static DrawablePart[] emptyDrawablePartArray = {};

		public void draw(Graphics g);
	}

	@SuppressWarnings("rawtypes")
	public static BodyPart[] emptyBodyPartArray = {};

	public SensorInput[] getInputs();

	public ActorOutput[] getOutputs();

	public void sampleInputs();

	public void applyOutputs();

	public void addToWorld(E world);
}