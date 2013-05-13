package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.genetics.lgp.EvoVMProgramGene;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.World2d;
public class AntGenome implements Genome
{
	private final BodyPart.BodyPartGene<World2d> bodyGene;

	public final EvoVMProgramGene brainGene;


	public AntGenome(BodyPart.BodyPartGene<World2d> bodyGene, EvoVMProgramGene brainGene)
	{
		this.bodyGene = bodyGene;
		this.brainGene = brainGene;
	}

	public AntGenome(BodyPart.BodyPartGene<World2d> bodyGene, int maxLength)
	{
		this.bodyGene = bodyGene;
		this.brainGene = EvoVMProgramGene.randomGene(maxLength);
	}

	@Override
	public AntGenome replicate()
	{
		return new AntGenome(bodyGene, brainGene.replicate());
	}

	@Override
	public int size()
	{
		return brainGene.size();
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
	public Organism synthesize()
	{
		// create organism
		Organism organism = new Organism(this);

		// create and attach body
		BodyPart<World2d>[] bodies = new BodyPart[] { bodyGene.express(organism) };
		organism.setIOUnits(bodies);

		// create and attach brain
		organism.setVM(brainGene.express(organism));
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// return assembled organism
		return organism;
	}

}
