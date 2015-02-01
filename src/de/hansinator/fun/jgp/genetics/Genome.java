package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genome is a Gene-tree with fitness
 * @author hansinator
 *
 */
public class Genome
{
	private final ExecutionUnit.Gene<World2d> rootGene;

	private int fitness;
	
	public Genome(ExecutionUnit.Gene<World2d> rootGene)
	{
		this.rootGene = rootGene;
	}
	
	public int getFitness()
	{
		return fitness;
	}

	//XXX make this something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	public void setFitness(int fitness)
	{
		this.fitness = fitness;
	}
	
	public ExecutionUnit.Gene<World2d> getRootGene()
	{
		return rootGene;
	}
}
