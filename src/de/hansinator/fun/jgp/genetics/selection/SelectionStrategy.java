/*
 */

package de.hansinator.fun.jgp.genetics.selection;

import de.hansinator.fun.jgp.genetics.Genome;

/**
 * 
 * @author Hansinator
 */
public interface SelectionStrategy
{

	public Genome select(Genome[] pool, int totalFitness);
}
