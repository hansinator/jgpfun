package de.hansinator.fun.jgp.life;

public interface ExecutionUnit
{
	public void setInputs(SensorInput[] inputs);

	public void setOutputs(ActorOutput[] outputs);

	public void run();

	public int getProgramSize();

	public int getInputCount();

	@SuppressWarnings("rawtypes")
	public interface Gene extends de.hansinator.fun.jgp.genetics.Gene<ExecutionUnit, Organism>
	{
		@Override
		public Gene replicate();

		public int size();
	}
}
