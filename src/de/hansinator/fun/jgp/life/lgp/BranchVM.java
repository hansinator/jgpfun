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
		super(numRegs, program);
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
			final Instruction curop = program[pc++];

			if (curop instanceof BranchOperation)
			{
				if (curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2])) != 1)
					pc++;
			} else // execute the operation
				regs[curop.trg] = curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
		}

		// write output values
		for (ActorOutput out : outputs)
			out.set(regs[reg++]);
	}
}
