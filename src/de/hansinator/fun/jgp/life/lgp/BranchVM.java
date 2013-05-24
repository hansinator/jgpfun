package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.life.lgp.operations.BranchOperation;

/**
 * 
 * @author hansinator
 */
public class BranchVM extends LGPMachine
{
	private final OpCode[] program;

	private SensorInput[] inputs = SensorInput.emptySensorInputArray;

	private ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	public BranchVM(int numRegs, int numInputRegs, OpCode[] program)
	{
		regs = new int[numRegs];

		// normalize program and strip strctural intron code portions
		this.program = EvoCodeUtils
				.stripStructuralIntronCode(normalizeProgram(program, numRegs), numRegs, numInputRegs);
	}

	@Override
	public void run()
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

	@Override
	public int getProgramSize()
	{
		return program.length;
	}

	@Override
	public void setInputs(SensorInput[] inputs)
	{
		this.inputs = inputs;
	}

	@Override
	public void setOutputs(ActorOutput[] outputs)
	{
		this.outputs = outputs;
	}

	@Override
	public int getInputCount()
	{
		return inputs.length;
	}
}
