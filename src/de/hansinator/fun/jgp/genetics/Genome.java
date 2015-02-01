package de.hansinator.fun.jgp.genetics;

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
		//XXX TODO
		//Walk the gene tree and collect possible mutations in a list than use roulette selector
	}
}
