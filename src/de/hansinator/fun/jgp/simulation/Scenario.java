package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.life.OrganismGene;

/*
 * maybe create a line following scenario
 */
public interface Scenario
{

	public WorldSimulation getSimulation();

	public OrganismGene randomGenome();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}