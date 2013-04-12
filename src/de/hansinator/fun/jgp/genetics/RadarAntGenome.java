package de.hansinator.fun.jgp.genetics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.FoodAntBody;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

public class RadarAntGenome extends Genome
{

	private static int NUM_INPUTS = 5;

	private static int NUM_OUTPUTS = 3;

	public RadarAntGenome(List<OpCode> program)
	{
		super(program);
	}

	@Override
	public Genome clone()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.clone());

		return new RadarAntGenome(p);
	}

	@Override
	public Body2d synthesizeBody(Organism2d organism, World2d world)
	{
		// create body without builtin locator usage and add parts
		Body2d body = new FoodAntBody(organism, world, NUM_INPUTS, NUM_OUTPUTS, false);
		body.addBodyPart(new RadarSense(body, world));
		body.addBodyPart(body.new OrientationSense());
		body.addBodyPart(body.new SpeedSense());
		body.addBodyPart(new WallSense(body, world));
		body.addBodyPart(new TankMotor(body));
		return body;
	}

	@Override
	public Organism2d synthesize()
	{
		final int numBodies = 1;
		final int numInputs = NUM_INPUTS * numBodies;
		final int numOutputs = NUM_OUTPUTS * numBodies;
		BaseMachine brain = new EvoVM(registerCount, numInputs, program.toArray(new OpCode[program.size()]));

		try
		{
			return new Organism2d(this, brain, numBodies, numInputs, numOutputs);
		} catch (IOException ex)
		{
			Logger.getLogger(Genome.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	public static Genome randomGenome(int progSize)
	{
		int size = rnd.nextInt(progSize - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new RadarAntGenome(program);
	}
}
