package de.hansinator.fun.jgp.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.OrganismGene;

/**
 * 
 * @author hansinator
 */
public class PoolingPopulationManager
{

	public static final int maxPoolSize = 26;

	private List<Organism> organismPool;

	private int bestInPool;

	private int foodCollected;

	private int totalFit;

	public PoolingPopulationManager(int popSize, int progSize)
	{
		organismPool = new ArrayList<Organism>(maxPoolSize);
	}


	// this funtion ensures that the populatio pool
	// does not exceed it's maximum size by purging
	// the least successful organisms

	private void updatePool()
	{
		if (organismPool.size() > maxPoolSize)
		{
			// sort the list so that the fittest organisms are on top
			Collections.sort(organismPool);
			Collections.reverse(organismPool);

			// drop all superfluous organisms
			for (int i = organismPool.size() - 1; i > (maxPoolSize - 1); i--)
				organismPool.remove(i);
		}
	}

	public int newGeneration()
	{
		double mutador;
		OrganismGene parent1, parent2;
		List<Organism> newAnts = new ArrayList<Organism>(organisms.size());

		// enqueue all current organisms into our pool
		organismPool.addAll(organisms);

		// get the fitness
		// call order is important, because of:
		// TODO: global variable bestInPool...
		foodCollected = calculateFitness(organisms);
		totalFit = calculateFitness(organismPool);

		// create new genomes via cloning and mutation or crossover
		for (int i = 0; i < (organisms.size() / 2); i++)
		{
			// select two source genomes and clone them
			// note: you must copy/clone the genomes before modifying them,
			// as the genome is passed by reference
			parent1 = selector.select(organismPool).getGenome().replicate();
			parent2 = selector.select(organismPool).getGenome().replicate();

			// mutate or crossover with a user defined chance
			mutador = rnd.nextDouble();
			// if (mutador > crossoverRate) {
			// mutate genomes
			parent1.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
			parent2.mutate(rnd.nextInt(maxMutations) + 1, progSize, rnd);
			/*
			 * } else { //perform crossover crossover.cross(parent1, parent2,
			 * rnd); }
			 */

			// create new ants from the modified genomes and save them
			newAnts.add(parent1.synthesize());
			newAnts.add(parent2.synthesize());
		}

		// replace and leave the other to GC
		organisms = newAnts;

		// update the pool
		updatePool();

		return foodCollected;
	}

	// the team effort
	private int calculateFitness(List<Organism> organisms)
	{
		int totalFit = 0;
		bestInPool = 0;

		for (Organism o : organisms)
		{
			totalFit += o.getFitness();

			// remember the best
			if (o.getFitness() > bestInPool)
				bestInPool = o.getFitness();
		}

		return totalFit;
	}

}
