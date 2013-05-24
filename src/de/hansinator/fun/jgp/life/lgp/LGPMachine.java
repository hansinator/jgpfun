package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.ExecutionUnit;
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

/**
 * 
 * @author hansinator
 */
public abstract class LGPMachine implements ExecutionUnit
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

	protected static OpCode[] normalizeProgram(OpCode[] program, int numRegs)
	{
		for (int i = 0; i < program.length; i++)
		{
			OpCode curop = program[i];

			curop.src1 = Math.abs(curop.src1) % numRegs;
			curop.src2 = curop.immediate ? (curop.src2 / (int) Simulator.intScaleFactor)
					: (Math.abs(curop.src2) % numRegs);
			curop.trg = Math.abs(curop.trg) % numRegs;
			curop.op = Math.abs(curop.op) % ops.length;
			curop.operation = ops[curop.op];
		}

		return program;
	}
}
