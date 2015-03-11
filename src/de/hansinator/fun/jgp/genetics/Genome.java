package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.selection.Selectable;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
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
	private final ExecutionUnit.Gene<World2d> rootGene;
	
	//XXX make this something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	private final FitnessEvaluator fitnessEvaluator;
	
	private final SelectionStrategy mutationSelector;
	
	public Genome(ExecutionUnit.Gene<World2d> rootGene, FitnessEvaluator fitnessEvaluator, SelectionStrategy mutationSelector)
	{
		this.rootGene = rootGene;
		this.fitnessEvaluator = fitnessEvaluator;
		this.mutationSelector = mutationSelector;
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
		return new Genome(rootGene.replicate(), fitnessEvaluator.replicate(), mutationSelector);
	}
	
	public void mutate(Random rng, int mutationCount)
	{
		// walk the gene tree and collect possible mutations in a list
		List<Mutation> mutations = new ArrayList<Mutation>();
		collectMutations(rootGene, mutations);

		// select given amount of mutations
		for(int i = 0; i < mutationCount; i++)
		{
			// always sum up chances anew because they might change during runs
			int totalFitness = 0;
			for (Mutation mutation : mutations)
				totalFitness += mutation.getMutationChance();
			
			// use our selector to select a mutation and execute it
			mutationSelector.select(mutations.toArray(new Mutation[mutations.size()]), totalFitness).mutate(rng);
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
