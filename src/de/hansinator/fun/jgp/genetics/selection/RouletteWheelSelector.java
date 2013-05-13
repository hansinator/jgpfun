/*
 */
package de.hansinator.fun.jgp.genetics.selection;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.Organism;
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
	public Organism select(List<Organism> organisms, int totalFitness)
	{
		int stopPoint = 0;
		int fitnessSoFar = 0;

		if (totalFitness > 0)
			stopPoint = rnd.nextInt(totalFitness);
		else return organisms.get(rnd.nextInt(organisms.size()));

		/*
		 * Shuffle the organism list to make roulettewheel work better. In case
		 * this method is called multiple times on the same list, the same
		 * organisms with a huge fitness values at the beginning of the list
		 * would have a greater chance of being selected. This shuffle hopefully
		 * eliminates this problem, if it does exist.
		 */
		Collections.shuffle(organisms);

		for (int i = 0; i < organisms.size(); i++)
		{
			fitnessSoFar += organisms.get(i).getFitness();
			// this way zero fitness ants are omitted
			if (fitnessSoFar > stopPoint)
				return organisms.get(i);
		}

		return organisms.get(rnd.nextInt(organisms.size()));
	}

}
