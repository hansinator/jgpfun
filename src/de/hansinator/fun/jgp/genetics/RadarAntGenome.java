package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.FoodAntBody;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

public class RadarAntGenome extends Genome
{

	private static int NUM_INPUTS = 5;

	private static int NUM_OUTPUTS = 3;

	public RadarAntGenome(List<OpCode> program, int maxLength)
	{
		super(program, maxLength);
	}

	public RadarAntGenome(int maxLength)
	{
		super(maxLength);
	}

	@Override
	public Genome clone()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.clone());

		return new RadarAntGenome(p, maxLength);
	}

	@Override
	public Organism2d synthesize()
	{
		final int numBodies = 1;
		final int numInputs = NUM_INPUTS * numBodies;
		
		// create brain
		BaseMachine brain = new EvoVM(registerCount, numInputs, program.toArray(new OpCode[program.size()]));
		
		// create organism
		Organism2d organism = new Organism2d(this, brain);
		
		// create body without builtin locator usage and add parts
		Body2d body = new FoodAntBody(organism, NUM_INPUTS, NUM_OUTPUTS, false);
		body.addBodyPart(new RadarSense(body));
		body.addBodyPart(body.new OrientationSense());
		body.addBodyPart(body.new SpeedSense());
		body.addBodyPart(new WallSense(body));
		body.addBodyPart(new TankMotor(body));
		
		// attach body
		organism.setBodies(new Body2d[] { body });

		// return assembled organism
		return organism;
	}
}
