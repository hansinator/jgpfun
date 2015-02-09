package de.hansinator.fun.jgp.genetics;

import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.util.Settings;

public abstract class ValueGene<V> extends MutableGene<V, Object> {
	protected V value;

	public ValueGene(V value, int mutationChance) {
		super(mutationChance);
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public Gene<V, Object> replicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V express(Object context) {
		return value;
	}

	@Override
	public List<Gene> getChildren() {
		return null;
	}

	public static class IntegerGene extends ValueGene<Integer> {
		private static final Random rnd = Settings.newRandomSource();

		public IntegerGene(int mutationChance) {
			super(0, mutationChance);
		}

		@Override
		public void mutate() {
			value = rnd.nextInt();
		}
	}

	public static class BooleanGene extends ValueGene<Boolean> {
		private static final Random rnd = Settings.newRandomSource();

		public BooleanGene(int mutationChance) {
			super(false, mutationChance);
		}

		@Override
		public void mutate() {
			value = rnd.nextBoolean();
		}
	}
}