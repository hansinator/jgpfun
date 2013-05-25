package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.world.World;


/**
 * 
 * @author hansinator
 */
public interface SensorInput
{

	public static final SensorInput[] emptySensorInputArray = {};

	public int get();

	public interface Gene<E extends World> extends de.hansinator.fun.jgp.genetics.Gene<SensorInput, IOUnit<E>>
	{
		@Override
		public Gene<E> replicate();

		@Override
		public SensorInput express(IOUnit<E> context);
	}
}
