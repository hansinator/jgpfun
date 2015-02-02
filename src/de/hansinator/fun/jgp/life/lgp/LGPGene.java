package de.hansinator.fun.jgp.life.lgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.AbstractMutation;
import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genetic representation of an LGP program. Supports mutations
 * for insertion, removal and replacement of OpCodes to the program.
 * 
 * @author hansinator
 *
 */
public class LGPGene implements ExecutionUnit.Gene<World2d>
{
	private static final Random rnd = Settings.newRandomSource();
	
	// define chances for what mutation could happen in some sort of
	// percentage
	private static final int mutateIns = 22, mutateRem = 18, mutateRep = 20;
	
	static final int registerCount = Settings.getInt("registerCount");

	private final List<OpCode> program;

	private final int maxLength;
	
	public final List<IOUnit.Gene<ExecutionUnit<World2d>>> ioGenes = new ArrayList<IOUnit.Gene<ExecutionUnit<World2d>>>();
	
	private int exonSize = 0;
	
	// insert a random instruction at a random location
	private final Mutation mutationInsert = new AbstractMutation(mutateIns) {
		
		@Override
		public void mutate()
		{
			program.add(rnd.nextInt(program.size()), OpCode.randomOpCode(rnd));
		}
	};
	
	// remove a random instruction
	private final Mutation mutationRemove = new AbstractMutation(mutateRem) {
		
		@Override
		public void mutate()
		{
			program.remove(rnd.nextInt(program.size()));
		}
	};
	
	// replace a random instruction
	private final Mutation mutationReplace = new AbstractMutation(mutateRep) {
		
		@Override
		public void mutate()
		{
			program.set(rnd.nextInt(program.size()), OpCode.randomOpCode(rnd));
		}
	};
	
	private final Mutation[] mutations = { mutationInsert, mutationRemove, mutationReplace };
	

	public static LGPGene randomGene(int maxLength)
	{
		int size = rnd.nextInt(maxLength - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new LGPGene(program, maxLength);
	}


	private LGPGene(List<OpCode> program, int maxLength)
	{
		this.program = program;
		this.maxLength = maxLength;
	}

	@Override
	public LGPGene replicate()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.replicate());

		LGPGene lg = new LGPGene(p, maxLength);
		
		for(IOUnit.Gene<ExecutionUnit<World2d>> bg : ioGenes)
			lg.ioGenes.add(bg.replicate());
		
		return lg;
	}

	@Override
	public ExecutionUnit<World2d> express(World2d context)
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


	@SuppressWarnings("rawtypes")
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

	@Override
	public Mutation[] getMutations()
	{
		// if we have the max possible opcodes, we can't add a new one,
		// so adjust insert mutation chance accordingly
		mutationInsert.setMutationChance((program.size() >= maxLength)?0:mutateIns);
		
		// if we have only 4 opcodes left, don't delete more
		if (program.size() < 5)
		{
			mutationRemove.setMutationChance(0);
			
			// higher ins chance test: when prog is too small, mutation tends to vary the same loc multiple times,
			// so insert some more
			mutationInsert.setMutationChance(100);
		}
		else mutationRemove.setMutationChance(mutateRem);
				
		return mutations;
	}
	
}
