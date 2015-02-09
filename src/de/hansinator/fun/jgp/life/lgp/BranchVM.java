package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.life.lgp.operations.BranchOperation;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public class BranchVM<E extends World> extends LGPMachine<E>
{

	public BranchVM(int numRegs, OpCode[] program)
	{
		// normalize program
		super(numRegs, normalizeProgram(program, numRegs));
	}

	@Override
	protected void step()
	{
		int reg = 0, pc = 0;

		// write input registers
		for (SensorInput in : inputs)
			regs[reg++] = in.get();

		while (pc < program.length)
		{
			final OpCode curop = program[pc++];

			if (curop instanceof BranchOperation)
			{
				if (curop.operation.execute(regs[curop.src1.getValue()], (curop.immediate.getValue() ? curop.src2.getValue() : regs[curop.src2.getValue()])) != 1)
					pc++;
			} else // execute the operation
				regs[curop.trg.getValue()] = curop.operation.execute(regs[curop.src1.getValue()], (curop.immediate.getValue() ? curop.src2.getValue()
						: regs[curop.src2.getValue()]));
		}

		// write output values
		for (ActorOutput out : outputs)
			out.set(regs[reg++]);
	}
}
