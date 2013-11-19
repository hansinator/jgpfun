package de.hansinator.fun.jgp.life.lgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.OrganismGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

public class LGPGene extends OrganismGene<World2d>
{
	private static final Random rnd = Settings.newRandomSource();

	private final List<OpCode> program;

	private final int maxLength;

	static final int registerCount = Settings.getInt("registerCount");
	

	// define chances for what mutation could happen in some sort of
	// percentage
	private static int mutateIns = 22, mutateRem = 18, mutateRep = 20;

	public static LGPGene randomGene(int maxLength)
	{
		int size = rnd.nextInt(maxLength - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new LGPGene(program, maxLength, mutateIns + mutateRem + mutateRep);
	}


	private LGPGene(List<OpCode> program, int maxLength, int mutationChance)
	{
		super(mutationChance);
		this.program = program;
		this.maxLength = maxLength;
	}

	@Override
	public LGPGene replicate()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.replicate());

		return new LGPGene(p, maxLength, mutationChance);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ExecutionUnit express(Organism context)
	{
		return new EvoVM(this, registerCount, context.getInputCount(), program.toArray(new OpCode[program.size()]));
	}

	public int size()
	{
		return program.size();
	}
	
	@Override
	public void mutate() {
		int mutationChoice;
		int loc = rnd.nextInt(program.size());
		int mutateIns = LGPGene.mutateIns;
		int mutateRem = LGPGene.mutateRem;

		// now see what to do
		// either delete an opcode, add a new or mutate an existing one

		// first determine which mutations are possible and add up all the
		// chances
		// if we have the max possible opcodes, we can't add a new one
		if (program.size() >= maxLength)
			mutateIns = 0;
		// if we have only 4 opcodes left, don't delete more
		else if (program.size() < 5)
		{
			mutateRem = 0;

			// higher ins chance test: when prog is too small, mutation tends to vary the same loc multiple times, so insert some more
			mutateIns = 100;
		}

		// choose mutation
		mutationChoice = rnd.nextInt(mutateRep + mutateIns + mutateRem);

		// see which one has been chosen
		// insert a random instruction at a random location
		if (mutationChoice < mutateIns)
			program.add(loc, OpCode.randomOpCode(rnd));
		// remove a random instruction
		else if (mutationChoice < (mutateIns + mutateRem))
			program.remove(loc);
		// replace a random instruction
		else
			program.set(loc, OpCode.randomOpCode(rnd));
	}

	@Override
	public List<Gene<?, ?>> getChildren() {
		return program;
	}
}
