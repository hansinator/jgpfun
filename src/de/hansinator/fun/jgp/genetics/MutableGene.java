package de.hansinator.fun.jgp.genetics;


/**
 * This is a basic implementation of gene and mutation where the gene itself also
 * serves as it's only type of mutation to realize simple genes with a 1:1 relationship to mutations.  
 * 
 * @author hansinator
 *
 * @param <T>
 * @param <E>
 */
public abstract class MutableGene<T, E> extends AbstractMutation implements Gene<T, E> {
	
	private final Mutation[] mutations  = { this };
	
	public MutableGene(int mutationChance)
	{
		super(mutationChance);
	}
	
	@Override
	public Mutation[] getMutations()
	{
		return mutations;
	}
}
