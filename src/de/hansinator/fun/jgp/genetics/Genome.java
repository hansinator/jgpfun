package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.BaseOrganism;

public interface Genome
{
	public  Genome replicate();

	public int size();

	public void mutate(int mutCount);

	public BaseOrganism synthesize();
}
