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
	public EvoVM(int numRegs, int numInputRegs, OpCode[] program)
	{
		// normalize program and strip strctural intron code portions
		super(numRegs, EvoCodeUtils.stripStructuralIntronCode(normalizeProgram(program, numRegs), numRegs, numInputRegs));
	}

	@Override
	public void step()
	{
		int reg = 0, pc = 0;

		// write input registers
		for (SensorInput in : inputs)
			regs[reg++] = in.get();

		while (pc < program.length)
		{
			OpCode curop = program[pc++];
			regs[curop.trg.getValue()] = curop.operation.execute(regs[curop.src1.getValue()], (curop.immediate.getValue() ? curop.src2.getValue() : regs[curop.src2.getValue()]));
		}

		// write output values
		for (ActorOutput out : outputs)
			out.set(regs[reg++]);
	}
}
