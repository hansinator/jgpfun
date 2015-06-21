package de.hansinator.fun.jgp.simulation;

import javax.swing.JPanel;

import org.uncommons.watchmaker.framework.EvaluationStrategy;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import de.hansinator.fun.jgp.gui.EvoStats;

/*
 * maybe create a line following scenario
 */
public interface Scenario<T>
{
	JPanel getSimulationView();
	
	EvoStats getEvoStats();
	
	EvolutionEngine<T> getEngine();

	EvaluationStrategy<T> getEvaluationStrategy();

	AbstractCandidateFactory<T> getCandidateFactory();

	SelectionStrategy<Object> getSelectionStrategy();
	
	EvolutionaryProcess getEvolutionaryProcess();
}