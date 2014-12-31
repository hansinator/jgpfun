package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.BaseGene;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.world.world2d.World2d;

/*
 * maybe create a line following scenario
 */
public interface Scenario
{

	public WorldSimulation getSimulation();

	public BaseGene<ExecutionUnit<World2d>, World2d> randomGenome();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}