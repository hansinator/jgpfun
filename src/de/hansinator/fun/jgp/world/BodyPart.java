package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

import de.hansinator.fun.jgp.life.IOUnit;

/*
 * TODO: make an abstract BodyPart in this package that implements IOUnit
 * but unifies the duplicate code in clients of this current interface
 */
public interface BodyPart<E> extends IOUnit<E>
{
	@SuppressWarnings("rawtypes")
	public static BodyPart[] emptyBodyPartArray = {};

	public interface DrawablePart<E> extends BodyPart<E>
	{
		@SuppressWarnings("rawtypes")
		public static DrawablePart[] emptyDrawablePartArray = {};

		public void draw(Graphics g);
	}
}