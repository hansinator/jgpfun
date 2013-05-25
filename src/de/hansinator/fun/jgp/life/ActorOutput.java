package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.world.World;

public interface ActorOutput
{

	public static final ActorOutput[] emptyActorOutputArray = {};

	public void set(int value);

	public interface Gene<E extends World> extends de.hansinator.fun.jgp.genetics.Gene<ActorOutput, IOUnit<E>>
	{
		@Override
		public Gene<E> replicate();

		@Override
		public ActorOutput express(IOUnit<E> context);
	}
}
