package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.genetics.lgp.operations.UnaryOperation;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
public class AntGenome implements Genome
{
	private static final Random rnd = Settings.newRandomSource();

	private final int maxLength;

	private final Gene gene;

	static final int registerCount = Settings.getInt("registerCount");

	public final List<OpCode> program;


	public AntGenome(Gene gene, List<OpCode> program, int maxLength)
	{
		this.gene = gene;
		this.program = program;
		this.maxLength = maxLength;
	}

	public AntGenome(Gene gene, int maxLength)
	{
		this.gene = gene;
		this.maxLength = maxLength;
		int size = rnd.nextInt(maxLength - 200) + 201;
		program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));
	}

	@Override
	public AntGenome clone()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.clone());

		return new AntGenome(gene, p, maxLength);
	}

	@Override
	public int size()
	{
		return program.size();
	}

	// make random changes to random locations in the genome
	@Override
	public void mutate(int mutCount, Random rnd)
	{
		// determine amount of mutations, minimum 1
		// int mutCount = maxMutations;
		// int mutCount = randomR.Next(maxMutations) + 1;

		for (int i = 0; i < mutCount; i++)
			mutateProgramSpace(program, rnd);
	}

	private final static int maxRegisterValDelta = 16;

	private final static int maxConstantValDelta = 16384;

	// final int maxConstantValDelta = Integer.maxValue / 2;

	private void mutateProgramSpace(List<OpCode> program, Random rnd)
	{
		// define chances for what mutation could happen in some sort of
		// percentage
		int mutateIns = 22, mutateRem = 18, mutateRep = 20, mutateVal = 20;
		int mutateSrc2 = 20, mutateTrg = 20, mutateOp = 20, mutateFlags = 20;
		// chances sum represents 100%, i.e. the sum of all possible chances
		int chancesSum;
		// the choice of mutation
		int mutationChoice;
		// choose random location
		int loc = rnd.nextInt(program.size());
		// precalculate a random value, but exclude zero
		// zero is no valid constant, as it tends to create semantic introns
		// (like a = a + 0 or b = n * 0 and so on)
		int val = rnd.nextInt();
		// while (val == 0)
		// {
		// val = rnd.Next(Int32.MinValue, Int32.MaxValue);
		// }

		OpCode instr = program.get(loc);

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
			mutateIns = 100; // TEST: when prog is too small, mutation tends to
			// vary the same loc multiple times...
		}

		// if this is a unary op, don't touch src2 - it'll be noneffective
		if (instr instanceof UnaryOperation)
			mutateSrc2 = 0;

		// replacement is always possible..
		// add all up
		chancesSum = mutateRep + mutateIns + mutateRem + mutateVal + mutateSrc2 + mutateTrg + mutateOp + mutateFlags;

		// choose mutation
		mutationChoice = rnd.nextInt(chancesSum);

		// see which one has been chosen
		// mutate ins
		if (mutationChoice < mutateIns)
			// insert a random instruction at a random location
			program.add(loc, OpCode.randomOpCode(rnd));
		else if (mutationChoice < (mutateIns + mutateRem))
			// remove a random instruction
			program.remove(loc);
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep))
			// replace a random instruction
			program.set(loc, OpCode.randomOpCode(rnd));
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal))
		{
			// modify the src1 register number by a random value
			val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
			instr.src1 = (instr.src1 + val);

			// save modified instruction
			program.set(loc, instr);
		} // mutate src2
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2))
		{
			// if immediate, modify the constant value by random value
			if (instr.immediate)
			{
				val = rnd.nextInt(maxConstantValDelta * 2) - maxConstantValDelta;
				instr.src2 += val;
			} // else modify the src2 register number by a random value
			else
			{
				val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
				instr.src2 += val;
			}

			// save modified instruction
			program.set(loc, instr);
		} // mutate trg
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2 + mutateTrg))
		{
			// modify trg field by random value
			val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
			instr.trg = (instr.trg + val);

			// save modified instruction
			program.set(loc, instr);
		} // mutate op
		else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2 + mutateTrg + mutateOp))
		{
			// replace opcode field by random value
			instr.op = rnd.nextInt();

			// save modified instruction
			program.set(loc, instr);
		} // mutate opflags
		else
		{
			// set new random opflags
			instr.immediate = rnd.nextBoolean();

			// save modified instruction
			program.set(loc, instr);
		}
	}

	@Override
	public BaseOrganism synthesize()
	{
		// create organism
		Organism2d organism = new Organism2d(this);

		// create and attach body
		organism.setBodies(new Body2d[] { gene.createBody(organism) });

		// create and attach brain
		organism.setVM(new EvoVM(registerCount, organism.getInputCount(), program.toArray(new OpCode[program.size()])));
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// return assembled organism
		return organism;
	}

	public interface Gene
	{
		Body2d createBody(Organism2d organism);
	}
}
