package de.hansinator.fun.jgp.genetics;

import java.util.Random;

import de.hansinator.fun.jgp.life.BaseOrganism;

public interface Genome
{
	public abstract Genome clone();

	public int size();

	public void mutate(int mutCount, Random rnd);

	public BaseOrganism synthesize();
}
