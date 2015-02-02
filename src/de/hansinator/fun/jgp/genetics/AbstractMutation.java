package de.hansinator.fun.jgp.genetics;

public abstract class AbstractMutation implements Mutation
{
	protected int mutationChance;

	public AbstractMutation(int mutationChance) {
		setMutationChance(mutationChance);
	}

	public void setMutationChance(int mutationChance) {
		this.mutationChance = mutationChance;
	}

	public int getMutationChance() {
		return mutationChance;
	}
	
	public int getSelectionChance()
	{
		return getMutationChance();
	}
}
