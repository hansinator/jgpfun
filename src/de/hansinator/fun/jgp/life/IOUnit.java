package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.ImmutableGene;



public interface IOUnit<E>
{
	@SuppressWarnings("rawtypes")
	public static IOUnit[] emptyIOUnitArray = {};

	public void attachEvaluationState(E world);

	public void sampleInputs();

	public void applyOutputs();

	public SensorInput[] getInputs();

	public ActorOutput[] getOutputs();

	public abstract class Gene<E> extends ImmutableGene<IOUnit<E>, E>
	{
		@Override
		public abstract Gene<E> replicate();

		@Override
		public abstract IOUnit<E> express(E context);
		
		public abstract int getInputCount();

		public abstract int getOutputCount();
	}
}
