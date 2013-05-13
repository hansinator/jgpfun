package de.hansinator.fun.jgp.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.data.xy.XYSeries;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.world.world2d.Organism2d;

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
		super(popSize, progSize);
		organismPool = new ArrayList<Organism>(maxPoolSize);
	}

	public void printStats(StatisticsHistoryModel statisticsHistory, int totalFood, int generation,
			XYSeries progSizeChartData, XYSeries realProgSizeChartData)
	{
		int avgProgSize = 0, avgRealProgSize = 0;

		// pool statistics
		for (Organism o : organismPool)
			avgProgSize += o.getGenome().program.size();
		avgProgSize /= (organismPool.size() > 0) ? organismPool.size() : 1;

		for (Organism o : organismPool)
			avgRealProgSize += ((Organism2d) o).vm.getProgramSize();
		avgRealProgSize /= (organismPool.size() > 0) ? organismPool.size() : 1;

		System.out.println("Avg pool prg size (cur gen): " + avgProgSize);
		System.out.println("Avg real pool prg size (cur gen): " + avgRealProgSize);
		System.out.println("Pool food: " + totalFit);
		System.out.println("Pool avg food: " + (totalFit / ((organismPool.size() > 0) ? organismPool.size() : 1)));
		System.out.println("Best in pool: " + bestInPool);

		// generation statistics
		avgProgSize = 0;
		for (Organism o : organisms)
			avgProgSize += o.getGenome().program.size();
		avgProgSize /= organisms.size();

		avgRealProgSize = 0;
		for (Organism o : organisms)
			avgRealProgSize += ((Organism2d) o).vm.getProgramSize();
		avgRealProgSize /= organisms.size();

		statisticsHistory
		.appendEntry(generation, totalFood, totalFood / organisms.size(), avgProgSize, avgRealProgSize);
		progSizeChartData.add(generation, avgProgSize);
		realProgSizeChartData.add(generation, avgRealProgSize);
	}

	private void printPool()
	{
		System.out.println("Pool:");
		for (int i = 0; i < organismPool.size(); i++)
			System.out.println("" + i + ":\t" + organismPool.get(i).getFitness());
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
		AntGenome parent1, parent2;
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

	public int getCurrentPopulationFitness()
	{
		return totalFit;
	}

}
