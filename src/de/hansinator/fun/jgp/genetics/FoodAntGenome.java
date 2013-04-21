package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.Part;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

public class FoodAntGenome extends Genome
{

	private static int NUM_INPUTS = 7;
	

	public FoodAntGenome(List<OpCode> program, int maxLength)
	{
		super(program, maxLength);
	}
	
	public FoodAntGenome(int maxLength)
	{
		super(maxLength);
	}

	@Override
	public Genome clone()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.clone());

		return new FoodAntGenome(p, maxLength);
	}

	@Override
	public Organism2d synthesize()
	{
		final int numBodies = 1;
		final int numInputs = NUM_INPUTS * numBodies;
		
		// create brain
		BaseMachine brain = new EvoVM(registerCount, numInputs, program.toArray(new OpCode[program.size()]));
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));
		
		// create organism
		Organism2d organism = new Organism2d(this, brain);
		
		// create body and attach parts
		AntBody body = new AntBody(organism);
		final Part[] parts = new Part[] { body.locator, body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body) };
		body.setParts(parts);

		// attach body
		organism.setBodies(new Body2d[] { body });

		// return assembled organism
		return organism;
	}
}
