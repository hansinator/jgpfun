package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;

/*
 * XXX refactor this into a Gene (OrganismGene) somehow
 */
public class Genome
{

	private final ExecutionUnit.Gene brainGene;

	private final IOUnit.Gene bodyGene;

	private int fitness;

	private int exonSize;

	public Genome(IOUnit.Gene bodyGene, ExecutionUnit.Gene brainGene)
	{
		this.brainGene = brainGene;
		this.bodyGene = bodyGene;
	}


	public Genome replicate()
	{
		return new Genome(bodyGene, brainGene.replicate());
	}

	// make random changes to random locations in the genome
	public void mutate(int mutCount)
	{
		// determine amount of mutations, minimum 1
		// int mutCount = maxMutations;
		// int mutCount = randomR.Next(maxMutations) + 1;

		for (int i = 0; i < mutCount; i++)
			brainGene.mutate();
	}

	@SuppressWarnings("rawtypes")
	public IOUnit.Gene getBodyGene()
	{
		return bodyGene;
	}

	public ExecutionUnit.Gene getBrainGene()
	{
		return brainGene;
	}

	public int size()
	{
		return brainGene.size();
	}

	public int getFitness()
	{
		return fitness;
	}

	//XXX make thism something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	public void setFitness(int fitness)
	{
		this.fitness = fitness;
	}

	public int getExonSize()
	{
		return exonSize;
	}

	public void setExonSize(int exonSize)
	{
		this.exonSize = exonSize;
	}
}