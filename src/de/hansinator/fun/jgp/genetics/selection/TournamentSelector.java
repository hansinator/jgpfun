/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.util.Random;

import de.hansinator.fun.jgp.life.OrganismGene;
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
	public OrganismGene select(OrganismGene[] pool, int totalFitness)
	{
		int maxFit = -1;
		int size;
		OrganismGene fittest = null;

		if (pool.length < tournamentSize)
			size = pool.length;
		else size = tournamentSize;

		if (size == 0)
			return null;

		for (int i = 0; i < size; i++)
		{
			OrganismGene candidate = pool[rnd.nextInt(pool.length)];

			if (candidate.getFitness() > maxFit)
			{
				maxFit = candidate.getFitness();
				fittest = candidate;
			}
		}

		return fittest;
	}

}
