package de.hansinator.fun.jgp.genetics.lgp;

import java.util.Random;

import de.hansinator.fun.jgp.genetics.lgp.operations.Operation;
import de.hansinator.fun.jgp.genetics.lgp.operations.UnaryOperation;
import de.hansinator.fun.jgp.util.Settings;

public class OpCode
{

	private static final Random rnd = Settings.newRandomSource();

	private final static int maxRegisterValDelta = 16;

	private final static int maxConstantValDelta = 16384;

	private int mutateVal = 20, mutateSrc2 = 20, mutateTrg = 20, mutateOp = 20, mutateFlags = 20;

	//sum represents 100%, i.e. the sum of all possible chances
	public int totalMutate = mutateVal + mutateSrc2 + mutateTrg + mutateOp + mutateFlags;

	int op;

	int src1;

	int src2;

	int trg;

	boolean immediate;

	Operation operation;

	public static OpCode randomOpCode(Random rnd)
	{
		return new OpCode(rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextBoolean());
	}


	private OpCode(int op, int src1, int src2, int trg, boolean immediate)
	{
		this.op = op;
		this.src1=src1;
		this.src2=src2;
		this.trg=trg;
		this.immediate=immediate;

		// if this is a unary op, don't touch src2 - it'll be noneffective
		//XXX see xxx below
		if (BaseMachine.ops[Math.abs(op) % BaseMachine.ops.length] instanceof UnaryOperation)
			mutateSrc2 = 0;
	}

	public OpCode replicate()
	{
		return new OpCode(op, src1, src2, trg, immediate);
	}

	public void mutate()
	{
		int mutationChoice = rnd.nextInt(totalMutate);

		// if this is a unary op, don't touch src2 - it'll be noneffective
		//XXX duplicate - solve by making this class immutable and returning a new opcode
		//or by having a set chance of opcode mutation in the "mother" gene that is not influenced by this
		if (BaseMachine.ops[Math.abs(op) % BaseMachine.ops.length] instanceof UnaryOperation)
			mutateSrc2 = 0;

		// see which one has been chosen
		// modify the src1 register number by a random value
		if (mutationChoice < (mutateVal))
			src1 += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		// mutate src2
		else if (mutationChoice < ( mutateVal + mutateSrc2))
		{
			// if immediate, modify the constant value by random value
			if (immediate)
				src2 += rnd.nextInt(maxConstantValDelta * 2) - maxConstantValDelta;
			// else modify the src2 register number by a random value
			else
				src2 += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		}
		// modify trg field by random value
		else if (mutationChoice < (mutateVal + mutateSrc2 + mutateTrg))
		{
			trg += rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
		}
		// replace opcode field by random value
		else if (mutationChoice < (mutateVal + mutateSrc2 + mutateTrg + mutateOp))
			op = rnd.nextInt();
		// set new random opflags
		else
			immediate = rnd.nextBoolean();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OpCode other = (OpCode) obj;
		if (this.op != other.op)
			return false;
		if (this.src1 != other.src1)
			return false;
		if (this.src2 != other.src2)
			return false;
		if (this.trg != other.trg)
			return false;
		if (this.immediate != other.immediate)
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 29 * hash + this.op;
		hash = 29 * hash + this.src1;
		hash = 29 * hash + this.src2;
		hash = 29 * hash + this.trg;
		hash = 29 * hash + (this.immediate ? 1 : 0);
		return hash;
	}

}
