package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.genetics.lgp.EvoVMProgramGene;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.BodyPart.BodyPartGene;

/*
 * XXX refactor this into a Gene (OrganismGene) somehow
 */
public class Genome
{

	public final EvoVMProgramGene brainGene;

	private final BodyPart.BodyPartGene bodyGene;

	private int fitness;

	private int exonSize;

	public Genome(BodyPart.BodyPartGene bodyGene, EvoVMProgramGene brainGene)
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
	public BodyPartGene getBodyGene()
	{
		return bodyGene;
	}

	public EvoVMProgramGene getBrainGene()
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