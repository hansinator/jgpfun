package de.hansinator.fun.jgp.life;

public interface ExecutionUnit
{
	public void setInputs(SensorInput[] inputs);

	public void setOutputs(ActorOutput[] outputs);

	public void run();

	public int getProgramSize();

	public int getInputCount();

	public interface Gene extends de.hansinator.fun.jgp.genetics.Gene<ExecutionUnit>
	{
		@Override
		public Gene replicate();

		public void mutate();

		public int size();
	}
}
