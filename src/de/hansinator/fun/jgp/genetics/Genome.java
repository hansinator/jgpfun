package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.Organism;

public interface Genome
{
	public  Genome replicate();

	public int size();

	public void mutate(int mutCount);

	public Organism synthesize();
}
