package de.hansinator.fun.jgp.life.lgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

public class LGPGene extends ExecutionUnit.Gene<World2d>
{
	private static final Random rnd = Settings.newRandomSource();

	private final List<OpCode> program;

	private final int maxLength;
	
	public final List<IOUnit.Gene<ExecutionUnit<World2d>>> ioGenes = new ArrayList<IOUnit.Gene<ExecutionUnit<World2d>>>();

	static final int registerCount = Settings.getInt("registerCount");
	
	private int exonSize = 0;
	

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

		LGPGene lg = new LGPGene(p, maxLength, mutationChance);
		
		for(IOUnit.Gene<ExecutionUnit<World2d>> bg : ioGenes)
			lg.ioGenes.add(bg.replicate());
		
		return lg;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ExecutionUnit express(World2d context)
	{
		int i = 0, o = 0, x;

		// count I/O ports
		for(IOUnit.Gene<ExecutionUnit<World2d>> ioGene : ioGenes)
		{
			i += ioGene.getInputCount();
			o += ioGene.getOutputCount();
		}
		
		// create IO port arrays
		SensorInput[] inputs = (i==0)?SensorInput.emptySensorInputArray:new SensorInput[i];
		ActorOutput[] outputs = (o==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[o];
		
		// create ExecutionUnit
		EvoVM<World2d> eu = new EvoVM<World2d>(registerCount, inputs.length, program.toArray(new OpCode[program.size()]));
		
		// update exon size
		exonSize = eu.getProgramSize();
		
		// create IO
		@SuppressWarnings("unchecked")
		IOUnit<ExecutionUnit<World2d>>[] bodies = new IOUnit[ioGenes.size()];
		for(i = 0; i < ioGenes.size(); i++)
			bodies[i] = ioGenes.get(i).express(eu);

		// attach bodies
		eu.setIOUnits(bodies);

		// collect IO ports
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}

		// attach IO ports to brain
		eu.setInputs(inputs);
		eu.setOutputs(outputs);

		// attach to evaluation state
		eu.setExecutionContext(context);

		// return assembled organism
		return eu;
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


	public List<Gene> getChildren() {
		List<Gene> list = new ArrayList<Gene>();
		list.addAll(program);
		return list;
	}


	@Override
	public int getExonSize()
	{
		return exonSize;
	}


	@Override
	public int getSize()
	{
		return program.size();
	}
	
}
