package de.hansinator.fun.jgp.genetics.lgp;

/**
 * 
 * @author hansinator
 */
public class EvoVM extends BaseMachine
{

	private int pc;

	private final OpCode[] program;

	public EvoVM(int numRegs, int numInputRegs, OpCode[] program)
	{
		regs = new int[numRegs];

		// normalize program and strip structural intron code portions
		this.program = EvoCodeUtils.stripStructuralIntronCode(normalizeProgram(program, numRegs), numRegs, numInputRegs);
		// this.program = normalizeProgram(program, numRegs);
	}

	@Override
	public void run()
	{
		pc = 0;
		while (pc < program.length)
		{
			OpCode curop = program[pc++];
			regs[curop.trg] = curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
		}
	}


	@Override
	public int getProgramSize()
	{
		return program.length;
	}

}
