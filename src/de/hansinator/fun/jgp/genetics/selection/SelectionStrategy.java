/*
 */

package de.hansinator.fun.jgp.genetics.selection;

import java.util.List;

import de.hansinator.fun.jgp.life.Organism;

/**
 * 
 * @author Hansinator
 */
public interface SelectionStrategy
{

	public Organism select(List<Organism> organisms, int totalFitness);
}
