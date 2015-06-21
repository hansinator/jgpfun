package de.hansinator.fun.jgp.simulation;

import org.uncommons.watchmaker.framework.EvaluationStrategy;
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

	EvaluationStrategy<T> createEvaluationStrategy();

	AbstractCandidateFactory<T> getCandidateFactory();

	SelectionStrategy<Object> getSelectionStrategy();

	EvolutionaryOperator<Genome> createEvolutionPipeline();
}