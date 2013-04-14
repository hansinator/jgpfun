package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;

public interface Scenario
{

	public WorldSimulation getSimulation();

	public Genome randomGenome();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}