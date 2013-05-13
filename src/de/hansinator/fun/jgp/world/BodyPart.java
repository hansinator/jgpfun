package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.Organism;

/*
 * TODO: make an abstract BodyPart in this package that implements IOUnit
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
		BodyPart<E> express(Organism organism);
	}
}