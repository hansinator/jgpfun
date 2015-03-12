package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genome is a Gene-tree with fitness
 * @author hansinator
 *
 */
public class Genome
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
	
	public static class GenomeMutation implements EvolutionaryOperator<Genome>
	{
		private final int maxMutations;
		
		public GenomeMutation(int maxMutations)
		{
			this.maxMutations = maxMutations;
		}
		
		@Override
		public List<Genome> apply(List<Genome> selectedCandidates, Random rng)
		{
			List<Genome> mutatedCandidates = new ArrayList<Genome>(selectedCandidates.size());
			
			// clone each candidate and mutate and return the children
			for (Genome parent : selectedCandidates)
			{
				Genome child = parent.replicate();
				child.mutate(rng, rng.nextInt(maxMutations) + 1);
				mutatedCandidates.add(child);
			}
			
			return mutatedCandidates;
		}	
	}
	
	public static class GenomeEvaluator implements org.uncommons.watchmaker.framework.FitnessEvaluator<Genome>
	{

		@Override
		public double getFitness(Genome candidate, List<? extends Genome> population)
		{
			return candidate.getFitnessEvaluator().getFitness();
		}

		@Override
		public boolean isNatural()
		{
			return true;
		}
		
	}
}
