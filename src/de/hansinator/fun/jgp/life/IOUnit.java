package de.hansinator.fun.jgp.life;



public interface IOUnit<E>
{
	@SuppressWarnings("rawtypes")
	public static IOUnit[] emptyIOUnitArray = {};

	public void attachEvaluationState(E world);

	public void sampleInputs();

	public void applyOutputs();

	public SensorInput[] getInputs();

	public ActorOutput[] getOutputs();

	public interface Gene<E> extends de.hansinator.fun.jgp.genetics.Gene<IOUnit<E>, E>
	{
		@Override
		public Gene<E> replicate();

		@Override
		public IOUnit<E> express(E context);
	}
}
