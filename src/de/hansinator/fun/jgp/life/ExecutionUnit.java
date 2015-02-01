package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.BaseGene;

public interface ExecutionUnit<E>
{
	public void setInputs(SensorInput[] inputs);

	public void setOutputs(ActorOutput[] outputs);

	public void execute();

	public int getProgramSize();

	public int getInputCount();
	
	public BaseGene<ExecutionUnit<E>, E> getGenome();
	
	public void setExecutionContext(E context);
	
	public E getExecutionContext();
}
