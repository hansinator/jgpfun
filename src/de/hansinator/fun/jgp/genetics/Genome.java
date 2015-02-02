package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.selection.Selectable;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genome is a Gene-tree with fitness
 * @author hansinator
 *
 */
public class Genome implements Selectable
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
	
	@Override
	public int getSelectionChance()
	{
		return fitnessEvaluator.getFitness();
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
		int totalFitness = 0;
		
		// walk the gene tree and collect possible mutations in a list
		List<Mutation> mutations = new ArrayList<Mutation>();
		collectMutations(rootGene, mutations);

		// sum up chances
		for (Mutation mutation : mutations)
			totalFitness += mutation.getMutationChance();
		
		// use a roulette-wheel selector to select mutations
		// TODO: de-duplicate roulette-wheel logic such that we can use selectors in general on this problem
		for(int i = 0; i < mutationCount; i++)
		{
			// drop the ball
			int stopPoint = rnd.nextInt(totalFitness);

			/*
			 * Shuffle the organism list to make roulette wheel work better. In case
			 * this method is called multiple times on the same list, the same
			 * organisms with a huge fitness values at the beginning of the list
			 * would have a greater chance of being selected.
			 */
			Collections.shuffle(mutations);

			// spin the wheel
			for (int x = 0, fitnessSoFar = 0; x < mutations.size(); x++)
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

			// if got here ball must have escaped rhoulette wheel
			// (or all ants have zero fitness)
			mutations.get(rnd.nextInt(mutations.size())).mutate();
		}
	}
	
	/**
	 * Recursively collect mutations from a Gene-tree
	 * 
	 * @param gene root of tree
	 * @param mutations list for storage of collected mutations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void collectMutations(Gene gene, List<Mutation> mutations)
	{
		// add this node's mutations
		for(Mutation m : gene.getMutations())
			if(m.getMutationChance() > 0)
				mutations.add(m);
		
		// recurse through children
		List<Gene> children = gene.getChildren();
		if(children != null)
			for(Gene child : children)
				collectMutations(child, mutations);
	}
}
