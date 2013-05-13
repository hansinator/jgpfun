/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.Organism;
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
	public Organism select(List<Organism> organisms, int totalFitness)
	{
		int maxFit = -1;
		int size;
		Organism fittest = null;

		if (organisms.size() < tournamentSize)
			size = organisms.size();
		else size = tournamentSize;

		if (size == 0)
			return null;

		for (int i = 0; i < size; i++)
		{
			Organism candidate = organisms.get(rnd.nextInt(organisms.size()));

			if (candidate.getFitness() > maxFit)
			{
				maxFit = candidate.getFitness();
				fittest = candidate;
			}
		}

		return fittest;
	}

}
