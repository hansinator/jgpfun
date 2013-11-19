/*
 */

package de.hansinator.fun.jgp.genetics.selection;

import de.hansinator.fun.jgp.life.OrganismGene;

/**
 * 
 * @author Hansinator
 */
public interface SelectionStrategy
{

	public OrganismGene select(OrganismGene[] pool, int totalFitness);
}
