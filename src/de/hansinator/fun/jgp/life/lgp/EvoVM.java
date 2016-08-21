package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public class EvoVM<E extends World> extends LGPMachine<E>
{
	public EvoVM(int numRegs, OpCode[] program)
	{
		super(numRegs, program);
	}

	@Override
	public void step()
	{
		int reg = 0, pc = 0;

		// write input registers
		for (SensorInput in : inputs)
			regs[reg++] = (int)Math.round(in.get());

		while (pc < program.length)
		{
			Instruction curop = program[pc++];
			regs[curop.trg] = curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
		}

		// write output values
		for (ActorOutput out : outputs)
			out.set(regs[reg++]);
	}
}
