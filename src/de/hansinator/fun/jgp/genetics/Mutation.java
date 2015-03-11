package de.hansinator.fun.jgp.genetics;

import java.util.Random;

import de.hansinator.fun.jgp.genetics.selection.Selectable;

/**
 * This describes a mutation that has a possibility to happen and can
 * be applied. Mutations are "offered" by Gene-instances and may be
 * selected and applied by a Gene-tree controller. One Gene may have
 * multiple mutations and one mutation should have only one Gene it
 * applies to.
 * 
 * @author hansinator
 *
 */
public interface Mutation extends Selectable
{
	public static Mutation[] emptyMutationArray = { };
	
	void mutate(Random rng);
	
	void setMutationChance(int mutationChance);
	
	int getMutationChance();
}
