package de.hansinator.fun.jgp.genetics;

public abstract class BaseGene<T, E> implements Gene<T, E> {
	protected int mutationChance;

	public BaseGene(int mutationChance) {
		setMutationChance(mutationChance);
	}

	public void setMutationChance(int mutationChance) {
		this.mutationChance = mutationChance;
	}

	public int getMutationChance() {
		return mutationChance;
	}
}
