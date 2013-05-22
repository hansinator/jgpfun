/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.util.Random;

import de.hansinator.fun.jgp.genetics.Genome;
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
	public Genome select(Genome[] pool, int totalFitness)
	{
		int maxFit = -1;
		int size;
		Genome fittest = null;

		if (pool.length < tournamentSize)
			size = pool.length;
		else size = tournamentSize;

		if (size == 0)
			return null;

		for (int i = 0; i < size; i++)
		{
			Genome candidate = pool[rnd.nextInt(pool.length)];

			if (candidate.getFitness() > maxFit)
			{
				maxFit = candidate.getFitness();
				fittest = candidate;
			}
		}

		return fittest;
	}

}
