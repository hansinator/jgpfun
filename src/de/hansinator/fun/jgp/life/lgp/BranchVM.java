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

	public BranchVM(OrganismGene<E> genome, FitnessEvaluator evaluator, int numRegs, int numInputRegs, OpCode[] program)
	{
		// normalize program and strip strctural intron code portions
		super(genome, evaluator, numRegs, EvoCodeUtils.stripStructuralIntronCode(normalizeProgram(program, numRegs), numRegs, numInputRegs));
	}

	@Override
	public void execute()
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
				if (curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2])) != 1)
					pc++;
			} else // execute the operation
				regs[curop.trg] = curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2
						: regs[curop.src2]));
		}

		// write output values
		for (ActorOutput out : outputs)
			out.set(regs[reg++]);
	}
}
