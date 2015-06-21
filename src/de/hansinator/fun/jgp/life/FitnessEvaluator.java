package de.hansinator.fun.jgp.life;


public interface FitnessEvaluator
{
	int getFitness();
	
	void setFitness(int fitness);

	FitnessEvaluator replicate();

	void attach(ExecutionUnit organism);
}
