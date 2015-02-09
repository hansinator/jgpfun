/*
 */

package de.hansinator.fun.jgp.genetics.selection;



/**
 * 
 * @author Hansinator
 */
public interface SelectionStrategy
{

	public <T extends Selectable> T select(T[] pool, int totalFitness);
}
