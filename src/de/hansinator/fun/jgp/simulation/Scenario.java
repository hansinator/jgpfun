package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;

/*
 * maybe create a line following scenario
 */
public interface Scenario
{

	public WorldSimulation getSimulation();

	public Genome randomGenome();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}