package de.hansinator.fun.jgp.life;


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
	
	public interface Gene<E> extends de.hansinator.fun.jgp.genetics.Gene<ExecutionUnit<E>, E>
	{
		@Override
		public Gene<E> replicate();

		@Override
		public ExecutionUnit<E> express(E context);
		
		public int getExonSize();

		public int getSize();
	}
}
