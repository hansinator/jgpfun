/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.util.Random;

import de.hansinator.fun.jgp.util.Settings;

/**
 * 
 * @author Hansinator
 */
public class TournamentSelector implements SelectionStrategy
{

	Random rnd = Settings.newRandomSource();

	final int tournamentSize;

	public TournamentSelector(int tournamentSize)
	{
		this.tournamentSize = tournamentSize;
	}

	@Override
	public <T extends Selectable> T select(T[] pool, int totalFitness)
	{
		int maxFit = -1;
		int size;
		T fittest = null;

		if (pool.length < tournamentSize)
			size = pool.length;
		else size = tournamentSize;

		if (size == 0)
			return null;

		for (int i = 0; i < size; i++)
		{
			T candidate = pool[rnd.nextInt(pool.length)];

			if (candidate.getSelectionChance() > maxFit)
			{
				maxFit = candidate.getSelectionChance();
				fittest = candidate;
			}
		}

		return fittest;
	}

}
