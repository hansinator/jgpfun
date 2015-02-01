package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genome is a Gene-tree with fitness
 * @author hansinator
 *
 */
public class Genome
{
	private final Random rnd = Settings.newRandomSource();
	
	private final ExecutionUnit.Gene<World2d> rootGene;
	
	//XXX make this something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	private final FitnessEvaluator fitnessEvaluator;
	
	public Genome(ExecutionUnit.Gene<World2d> rootGene, FitnessEvaluator fitnessEvaluator)
	{
		this.rootGene = rootGene;
		this.fitnessEvaluator = fitnessEvaluator;
	}
	
	public FitnessEvaluator getFitnessEvaluator()
	{
		return fitnessEvaluator;
	}
	
	public ExecutionUnit.Gene<World2d> getRootGene()
	{
		return rootGene;
	}
	
	public Genome replicate()
	{
		return new Genome(rootGene.replicate(), fitnessEvaluator.replicate());
	}
	
	public void mutate(int mutationCount)
	{
		for(int i = 0; i < mutationCount; i++)
		{
			// walk the gene tree and collect possible mutations in a list
			List<Gene> mutations = new ArrayList<Gene>();
			collectMutations(rootGene, mutations);
			
			// use a rhoulette-wheel selector to select a mutation
			// TODO: deduplicate rhoulette-wheel logic such that we can use selectors in general on this problem
			
			int stopPoint = 0;
			int fitnessSoFar = 0;
			int totalFitness = 0;

			// sum up chances
			for (Gene mutation : mutations)
				totalFitness += mutation.getMutationChance();
			
			stopPoint = rnd.nextInt(totalFitness);

			/*
			 * Shuffle the organism list to make roulettewheel work better. In case
			 * this method is called multiple times on the same list, the same
			 * organisms with a huge fitness values at the beginning of the list
			 * would have a greater chance of being selected. This shuffle hopefully
			 * eliminates this problem, if it does exist.
			 */
			Collections.shuffle(mutations);

			for (int x = 0; x < mutations.size(); x++)
			{
				fitnessSoFar += mutations.get(x).getMutationChance();
				// this way zero fitness ants are omitted
				if (fitnessSoFar > stopPoint)
				{
					// execute mutation and continue
					mutations.get(x).mutate();
					continue;
				}
			}

			// if we got here execute a random mutation
			mutations.get(rnd.nextInt(mutations.size())).mutate();
		}
	}
	
	private void collectMutations(Gene gene, List<Gene> mutations)
	{
		mutations.add(gene);
		List<Gene> children = gene.getChildren(); 
		if(children != null)
			for(Gene child : children)
				if(child.getMutationChance() > 0)
					collectMutations(child, mutations);
	}
}
