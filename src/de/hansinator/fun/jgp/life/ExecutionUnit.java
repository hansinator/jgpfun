package de.hansinator.fun.jgp.life;

public interface ExecutionUnit
{
	public void setInputs(SensorInput[] inputs);

	public void setOutputs(ActorOutput[] outputs);

	public void execute();

	public int getProgramSize();

	public int getInputCount();
}
