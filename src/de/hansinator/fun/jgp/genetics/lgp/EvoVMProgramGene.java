package de.hansinator.fun.jgp.genetics.lgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;

public class EvoVMProgramGene implements Gene<BaseMachine>
{
	private static final Random rnd = Settings.newRandomSource();

	private final List<OpCode> program;

	private final int maxLength;

	static final int registerCount = Settings.getInt("registerCount");

	// define chances for what mutation could happen in some sort of
	// percentage
	private int mutateIns = 22, mutateRem = 18, mutateRep = 20;

	public static EvoVMProgramGene randomGene(int maxLength)
	{
		int size = rnd.nextInt(maxLength - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new EvoVMProgramGene(program, maxLength);
	}


	private EvoVMProgramGene(List<OpCode> program, int maxLength)
	{
		this.program = program;
		this.maxLength = maxLength;
	}

	@Override
	public EvoVMProgramGene replicate()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.replicate());

		return new EvoVMProgramGene(p, maxLength);
	}

	@Override
	public BaseMachine express(BaseOrganism organism)
	{
		return new EvoVM(registerCount, organism.getInputCount(), program.toArray(new OpCode[program.size()]));
	}

	public int size()
	{
		return program.size();
	}

	public void mutate()
	{
		// chances sum represents 100%, i.e. the sum of all possible chances
		int chancesSum;
		// the choice of mutation
		int mutationChoice;
		// choose random location
		int loc = rnd.nextInt(program.size());

		// now see what to do
		// either delete an opcode, add a new or mutate an existing one

		// first determine which mutations are possible and add up all the
		// chances
		// if we have the max possible opcodes, we can't add a new one
		if (program.size() >= maxLength)
			mutateIns = 0;

		// if we have only 4 opcodes left, don't delete more
		if (program.size() < 5)
		{
			mutateRem = 0;

			// higher ins chance test: when prog is too small, mutation tends to vary the same loc multiple times, so insert some more
			mutateIns = 100;
		}

		OpCode instr = program.get(loc);

		// replacement is always possible..
		// add all up
		chancesSum = mutateRep + mutateIns + mutateRem + instr.totalMutate;

		// choose mutation
		mutationChoice = rnd.nextInt(chancesSum);

		// see which one has been chosen
		// insert a random instruction at a random location
		if (mutationChoice < mutateIns)
			program.add(loc, OpCode.randomOpCode(rnd));
		// remove a random instruction
		else if (mutationChoice < (mutateIns + mutateRem))
			program.remove(loc);
		// replace a random instruction
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep))
			program.set(loc, OpCode.randomOpCode(rnd));
		// muate instruction itself
		else
		{
			instr.mutate();
		}
	}
}
