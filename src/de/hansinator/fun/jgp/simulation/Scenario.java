package de.hansinator.fun.jgp.simulation;

import org.uncommons.watchmaker.framework.EvaluationStrategy;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;

/*
 * maybe create a line following scenario
 */
public interface Scenario<T>
{
	EvolutionEngine<T> getEngine();

	EvaluationStrategy<T> getEvaluationStrategy();

	AbstractCandidateFactory<T> getCandidateFactory();

	SelectionStrategy<Object> getSelectionStrategy();
	
	EvolutionaryProcess getEvolutionaryProcess();
}