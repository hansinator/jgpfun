package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVMProgramGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.World2d;
public class AntGenome extends Genome
{
	private final BodyPart.BodyPartGene<World2d> bodyGene;

	public AntGenome(BodyPart.BodyPartGene<World2d> bodyGene, EvoVMProgramGene brainGene)
	{
		super(brainGene);
		this.bodyGene = bodyGene;
	}

	public AntGenome(BodyPart.BodyPartGene<World2d> bodyGene, int maxLength)
	{
		super(EvoVMProgramGene.randomGene(maxLength));
		this.bodyGene = bodyGene;
	}

	@Override
	public AntGenome replicate()
	{
		return new AntGenome(bodyGene, brainGene.replicate());
	}

	// make random changes to random locations in the genome
	@Override
	public void mutate(int mutCount)
	{
		// determine amount of mutations, minimum 1
		// int mutCount = maxMutations;
		// int mutCount = randomR.Next(maxMutations) + 1;

		for (int i = 0; i < mutCount; i++)
			brainGene.mutate();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Organism<World2d> synthesize()
	{
		int i, o, x;

		// create organism
		Organism<World2d> organism = new Organism<World2d>(this);

		// create and attach body
		BodyPart<World2d>[] bodies = new BodyPart[] { bodyGene.express(organism) };
		organism.setIOUnits(bodies);

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

		// create brain
		BaseMachine vm = brainGene.express(organism);
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// attach inputs and outputs to brain
		vm.setInputs(inputs);
		vm.setOutputs(outputs);

		// attach brain
		organism.setVM(vm);

		// return assembled organism
		return organism;
	}


}
