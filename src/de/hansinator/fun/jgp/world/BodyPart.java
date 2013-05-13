package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.life.IOUnit;

/*
 * TODO: refactor this into a generic organismpart that resides in life package
 * and make an abstract BodyPart in this package that implements the generic iface
 * but unifies the duplicate code in clients of this current interface
 */
public interface BodyPart<E extends World> extends IOUnit<E>
{
	@SuppressWarnings("rawtypes")
	public static BodyPart[] emptyBodyPartArray = {};

	public interface DrawablePart<E extends World> extends BodyPart<E>
	{
		@SuppressWarnings("rawtypes")
		public static DrawablePart[] emptyDrawablePartArray = {};

		public void draw(Graphics g);
	}

	public interface BodyPartGene<E extends World> extends Gene<BodyPart<E>>
	{
		@Override
		BodyPart<E> express(BaseOrganism organism);
	}
}