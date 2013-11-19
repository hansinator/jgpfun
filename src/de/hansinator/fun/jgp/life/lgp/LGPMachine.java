package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.OrganismGene;
import de.hansinator.fun.jgp.life.lgp.operations.OpAbs;
import de.hansinator.fun.jgp.life.lgp.operations.OpAdd;
import de.hansinator.fun.jgp.life.lgp.operations.OpDiv;
import de.hansinator.fun.jgp.life.lgp.operations.OpMax;
import de.hansinator.fun.jgp.life.lgp.operations.OpMin;
import de.hansinator.fun.jgp.life.lgp.operations.OpMod;
import de.hansinator.fun.jgp.life.lgp.operations.OpMov;
import de.hansinator.fun.jgp.life.lgp.operations.OpMul;
import de.hansinator.fun.jgp.life.lgp.operations.OpNeg;
import de.hansinator.fun.jgp.life.lgp.operations.OpSqrt;
import de.hansinator.fun.jgp.life.lgp.operations.OpSub;
import de.hansinator.fun.jgp.life.lgp.operations.Operation;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public abstract class LGPMachine<E extends World> extends Organism<E>
{

	// compatible instruction set
	// static Operation[] ops = new Operation[]{new OpAdd(), new OpSub(), new OpMul(), new
	// OpDiv(), new OpMod()};

	//extended instruction set
	static Operation[] ops= new Operation[] { new OpAdd(), new OpSub(), new OpMul(), new OpDiv(), new OpMod(), new OpSqrt(),
		new OpNeg(), new OpMin(), new OpMax(), new OpAbs(),
		// new OpSin(),
		new OpMov(), //
		// new OpInc(),
		// new OpDec(),
		// new OpBranchLt(), new OpBranchGt()
		// new JumpOp(),
		// new JumpTarg()
	};

	public int[] regs;

	protected final OpCode[] program;

	public LGPMachine(OrganismGene<E> genome, FitnessEvaluator evaluator, int numRegs, OpCode[] program)
	{
		super(genome, evaluator);
		this.regs = new int[numRegs];
		this.program = program;
	}

	@Override
	public int getProgramSize()
	{
		return program.length;
	}

	protected static OpCode[] normalizeProgram(OpCode[] program, int numRegs)
	{
		for (int i = 0; i < program.length; i++)
		{
			OpCode curop = program[i];

			curop.src1.setValue(Math.abs(curop.src1.getValue()) % numRegs);
			curop.src2.setValue(curop.immediate.getValue() ? (curop.src2.getValue() / (int) Simulator.intScaleFactor)
					: (Math.abs(curop.src2.getValue()) % numRegs));
			curop.trg.setValue(Math.abs(curop.trg.getValue()) % numRegs);
			curop.op.setValue(Math.abs(curop.op.getValue()) % ops.length);
			curop.operation = ops[curop.op.getValue()];
		}

		return program;
	}
}
