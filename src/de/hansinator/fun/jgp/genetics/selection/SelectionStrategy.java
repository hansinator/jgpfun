/*
 */

package de.hansinator.fun.jgp.genetics.selection;


/**
 * 
 * @author Hansinator
 */
public interface SelectionStrategy
{

	public OrganismGene select(OrganismGene[] pool, int totalFitness);
}
