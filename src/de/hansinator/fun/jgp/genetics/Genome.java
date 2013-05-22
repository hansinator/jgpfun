package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.genetics.lgp.EvoVMProgramGene;
import de.hansinator.fun.jgp.life.Organism;

public abstract class Genome
{

	public final EvoVMProgramGene brainGene;

	private int fitness;

	private int exonSize;

	public Genome(EvoVMProgramGene brainGene)
	{
		this.brainGene = brainGene;
	}


	abstract public Genome replicate();

	abstract public void mutate(int mutCount);

	abstract public Organism synthesize();

	public int size()
	{
		return brainGene.size();
	}

	public int getFitness()
	{
		return fitness;
	}

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