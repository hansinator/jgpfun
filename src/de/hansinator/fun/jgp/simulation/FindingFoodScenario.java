/*
 */

package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.FoodAntGenome;
import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author Hansinator
 */
public class FindingFoodScenario implements Scenario
{
	private final int progSize = Settings.getInt("progSize");

	@Override
	public WorldSimulation getSimulation()
	{
		return new WorldSimulation(new World2d(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), Settings.getInt("foodCount")));
	}

	@Override
	public Genome randomGenome()
	{
		return new FoodAntGenome(progSize);
	}

	@Override
	public CrossoverOperator getCrossoverOperator()
	{
		return new OffsetTwoPointCrossover(progSize / 8);
	}

	@Override
	public SelectionStrategy getSelectionStrategy()
	{
		// TODO Auto-generated method stub
		return new TournamentSelector(3);
	}
}
