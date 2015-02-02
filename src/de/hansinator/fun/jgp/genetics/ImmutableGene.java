package de.hansinator.fun.jgp.genetics;

/**
 * This is a common base class for Genes that do not have mutations.
 * 
 * @author hansinator
 *
 * @param <T>
 * @param <E>
 */
public abstract class ImmutableGene<T, E> implements Gene<T, E>
{
	@Override
	public Mutation[] getMutations()
	{
		return Mutation.emptyMutationArray;
	}

}
