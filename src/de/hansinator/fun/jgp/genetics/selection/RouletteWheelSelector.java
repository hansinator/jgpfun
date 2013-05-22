/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.util.Settings;

/**
 * 
 * @author Hansinator
 */
public class RouletteWheelSelector implements SelectionStrategy
{

	final Random rnd = Settings.newRandomSource();

	/**
	 * fitness proportionate selection
	 */
	@Override
	public Genome select(Genome[] pool, int totalFitness)
	{
		int stopPoint = 0;
		int fitnessSoFar = 0;

		if (totalFitness > 0)
			stopPoint = rnd.nextInt(totalFitness);
		else return pool[rnd.nextInt(pool.length)];

		/*
		 * Shuffle the organism list to make roulettewheel work better. In case
		 * this method is called multiple times on the same list, the same
		 * organisms with a huge fitness values at the beginning of the list
		 * would have a greater chance of being selected. This shuffle hopefully
		 * eliminates this problem, if it does exist.
		 */
		Collections.shuffle(Arrays.asList(pool));

		for (int i = 0; i < pool.length; i++)
		{
			fitnessSoFar += pool[i].getFitness();
			// this way zero fitness ants are omitted
			if (fitnessSoFar > stopPoint)
				return pool[i];
		}

		return pool[rnd.nextInt(pool.length)];
	}

}
