package de.hansinator.fun.jgp.life;


public interface FitnessEvaluator
{
	int getFitness();

	FitnessEvaluator replicate();

	void attach(ExecutionUnit organism);
}
