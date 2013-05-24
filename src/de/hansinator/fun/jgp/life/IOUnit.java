package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.world.World;


public interface IOUnit<E extends World>
{
	@SuppressWarnings("rawtypes")
	public static IOUnit[] emptyIOUnitArray = {};

	public void attachEvaluationState(E world);

	public void sampleInputs();

	public void applyOutputs();

	public SensorInput[] getInputs();

	public ActorOutput[] getOutputs();

	public interface Gene<E extends World> extends de.hansinator.fun.jgp.genetics.Gene<IOUnit<E>>
	{
		@Override
		public Gene<E> replicate();

		@Override
		public IOUnit<E> express(Organism organism);
	}
}
