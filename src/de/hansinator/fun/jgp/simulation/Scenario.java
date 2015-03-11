package de.hansinator.fun.jgp.simulation;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;

/*
 * maybe create a line following scenario
 */
public interface Scenario<T>
{

	public WorldSimulation getSimulation();

	public AbstractCandidateFactory<T> getCandidateFactory();

	public CrossoverOperator getCrossoverOperator();

	public SelectionStrategy getSelectionStrategy();
}