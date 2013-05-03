package de.hansinator.fun.jgp.genetics;

import java.util.Random;

import de.hansinator.fun.jgp.genetics.lgp.EvoVMProgramGene;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
public class AntGenome implements Genome
{
	private static final Random rnd = Settings.newRandomSource();

	private final Body2d.Body2dGene bodyGene;

	public final EvoVMProgramGene brainGene;


	public AntGenome(Body2d.Body2dGene bodyGene, EvoVMProgramGene brainGene)
	{
		this.bodyGene = bodyGene;
		this.brainGene = brainGene;
	}

	public AntGenome(Body2d.Body2dGene bodyGene, int maxLength)
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

	@Override
	public BaseOrganism synthesize()
	{
		// create organism
		Organism2d organism = new Organism2d(this);

		// create and attach body
		organism.setBodies(new Body2d[] { bodyGene.express(organism) });

		// create and attach brain
		organism.setVM(brainGene.express(organism));
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// return assembled organism
		return organism;
	}
}
