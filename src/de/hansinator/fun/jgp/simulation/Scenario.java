package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;

public interface Scenario
{

	public WorldSimulation getSimulation();

	public AntGenome randomGenome();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}