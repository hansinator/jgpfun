package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.selection.Selectable;

public interface FitnessEvaluator extends Selectable
{
	int getFitness();

	FitnessEvaluator replicate();

	void attach(ExecutionUnit organism);
}
