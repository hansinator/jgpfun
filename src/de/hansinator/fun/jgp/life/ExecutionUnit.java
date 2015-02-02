package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.BaseGene;

public interface ExecutionUnit<E>
{
	public void setInputs(SensorInput[] inputs);

	public void setOutputs(ActorOutput[] outputs);
	
	public IOUnit<ExecutionUnit<E>>[] getIOUnits();

	public void execute();

	public int getProgramSize();

	public int getInputCount();
	
	public void setExecutionContext(E context);
	
	public E getExecutionContext();
	
	public abstract class Gene<E> extends BaseGene<ExecutionUnit<E>, E>
	{
		public Gene(int mutationChance)
		{
			super(mutationChance);
		}

		@Override
		public abstract Gene<E> replicate();

		@Override
		public abstract ExecutionUnit<E> express(E context);
		
		public abstract int getExonSize();

		public abstract int getSize();
	}
}
