package de.hansinator.fun.jgp.life.lgp;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.genetics.ImmutableGene;
import de.hansinator.fun.jgp.genetics.ValueGene;
import de.hansinator.fun.jgp.genetics.ValueGene.BooleanGene;
import de.hansinator.fun.jgp.genetics.ValueGene.IntegerGene;
import de.hansinator.fun.jgp.life.lgp.operations.Operation;
import de.hansinator.fun.jgp.life.lgp.operations.UnaryOperation;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

public class OpCode extends ImmutableGene<OpCode, LGPMachine<World2d>> {

	private static final Random rnd = Settings.newRandomSource();

	private final static int maxRegisterValDelta = 16;

	private final static int maxConstantValDelta = 16384;

	IntegerGene op = new IntegerGene(20) {
		@Override
		public void mutate() {
			value = rnd.nextInt();
		};
	};

	IntegerGene src1 = new IntegerGene(20) {
		@Override
		public void mutate() {
			value += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		};
	};

	IntegerGene src2 = new IntegerGene(20) {
		@Override
		public void mutate() {
			// if immediate, modify the constant value by random value
			if (immediate.getValue())
				value += rnd.nextInt(maxConstantValDelta * 2) - maxConstantValDelta;
			// else modify the src2 register number by a random value
			else
				value += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		};
	};

	IntegerGene trg = new IntegerGene(20) {
		@Override
		public void mutate() {
			value += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		};
	};

	BooleanGene immediate = new BooleanGene(20);
	
	@SuppressWarnings("rawtypes")
	Gene[] children = { op, src1, src2, trg, immediate };

	Operation operation;

	public static OpCode randomOpCode(Random rnd) {
		return new OpCode(rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextBoolean());
	}

	private OpCode(int op, int src1, int src2, int trg, boolean immediate) {
		this.op.setValue(op);
		this.src1.setValue(src1);
		this.src2.setValue(src2);
		this.trg.setValue(trg);
		this.immediate.setValue(immediate);

		// if this is a unary op, don't touch src2 - it'll be noneffective
		if (LGPMachine.ops[Math.abs(op) % LGPMachine.ops.length] instanceof UnaryOperation)
			this.src2.setMutationChance(0);
	}

	public OpCode replicate() {
		return new OpCode(op.getValue(), src1.getValue(), src2.getValue(), trg.getValue(), immediate.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OpCode other = (OpCode) obj;
		if (this.op.getValue() != other.op.getValue())
			return false;
		if (this.src1.getValue() != other.src1.getValue())
			return false;
		if (this.src2.getValue() != other.src2.getValue())
			return false;
		if (this.trg.getValue() != other.trg.getValue())
			return false;
		if (this.immediate.getValue() != other.immediate.getValue())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + this.op.getValue();
		hash = 29 * hash + this.src1.getValue();
		hash = 29 * hash + this.src2.getValue();
		hash = 29 * hash + this.trg.getValue();
		hash = 29 * hash + (this.immediate.getValue() ? 1 : 0);
		return hash;
	}

	@Override
	public OpCode express(LGPMachine<World2d> context) {
		return this;
	}

	@Override
	public List<Gene> getChildren() {
		return Arrays.asList(children);
	}
}
