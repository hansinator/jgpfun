package de.hansinator.fun.jgp.genetics;

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
public interface Mutation
{
	public static Mutation[] emptyMutationArray = { };
	
	void mutate();
	
	void setMutationChance(int mutationChance);
	
	int getMutationChance();
}
