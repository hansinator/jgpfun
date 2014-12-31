package de.hansinator.fun.jgp.life.lgp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.genetics.BaseGene;
import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

public class LGPGene extends BaseGene<ExecutionUnit<World2d>, World2d>
{
	private static final Random rnd = Settings.newRandomSource();

	private final List<OpCode> program;

	private final int maxLength;
	
	public final List<IOUnit.Gene<ExecutionUnit<World2d>>> ioGenes = new ArrayList<IOUnit.Gene<ExecutionUnit<World2d>>>();

	private final FitnessEvaluator fitnessEvaluator;
	
	private int fitness;

	private int exonSize;

	static final int registerCount = Settings.getInt("registerCount");
	

	// define chances for what mutation could happen in some sort of
	// percentage
	private static int mutateIns = 22, mutateRem = 18, mutateRep = 20;

	public static LGPGene randomGene(FitnessEvaluator evaluator, int maxLength)
	{
		int size = rnd.nextInt(maxLength - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new LGPGene(evaluator, program, maxLength, mutateIns + mutateRem + mutateRep);
	}


	private LGPGene(FitnessEvaluator evaluator, List<OpCode> program, int maxLength, int mutationChance)
	{
		super(mutationChance);
		this.fitnessEvaluator = evaluator;
		this.program = program;
		this.maxLength = maxLength;
	}

	@Override
	public LGPGene replicate()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.replicate());

		LGPGene lg = new LGPGene(fitnessEvaluator, p, maxLength, mutationChance);
		
		for(IOUnit.Gene<ExecutionUnit<World2d>> bg : ioGenes)
			lg.ioGenes.add(bg.replicate());
		
		return lg;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ExecutionUnit express(World2d context)
	{
		int i, o, x;

		// count I/O ports
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			i += bodies[x].getInputs().length;
			o += bodies[x].getOutputs().length;
		}

		// create I/O arrays
		SensorInput[] inputs = (i==0)?SensorInput.emptySensorInputArray:new SensorInput[i];
		ActorOutput[] outputs = (o==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[o];

		// collect I/O
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}
		
		// create ExecutionUnit
		EvoVM<World2d> eu = new EvoVM<World2d>(this, fitnessEvaluator, registerCount, inputs.length, program.toArray(new OpCode[program.size()]));
		
		// create io
		@SuppressWarnings("unchecked")
		IOUnit<World2d>[] bodies = new IOUnit[ioGenes.size()];
		for(i = 0; i < ioGenes.size(); i++)
			bodies[i] = ioGenes.get(i).express(eu);
		
		//XXX move this somewhere else
		//listen for collsions
		((Body2d)bodies[0]).addCollisionListener(fitnessEvaluator);
		
		// attach bodies
		eu.setIOUnits(bodies);

		// attach inputs and outputs to brain
		eu.setInputs(inputs);
		eu.setOutputs(outputs);

		//attach to evaluation state
		eu.addToWorld(context);

		// return assembled organism
		return eu;
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
	
	public int getFitness()
	{
		return fitness;
	}


	//XXX make thism something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	public void setFitness(int fitness)
	{
		this.fitness = fitness;
	}


	public int getExonSize()
	{
		return exonSize;
	}


	public void setExonSize(int exonSize)
	{
		this.exonSize = exonSize;
	}
	
}
