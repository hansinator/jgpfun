package de.hansinator.fun.jgp.genetics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.FoodAntBody;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

public class FoodAntGenome extends Genome
{

	private static int NUM_INPUTS = 7;

	private static int NUM_OUTPUTS = 2;

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
	public Body2d synthesizeBody(Organism2d organism, World world)
	{
		// create body and add parts
		FoodAntBody body = new FoodAntBody(organism, world, NUM_INPUTS, NUM_OUTPUTS, true);
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
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		try
		{
			return new Organism2d(this, brain, numBodies, numInputs, numOutputs);
		} catch (IOException ex)
		{
			Logger.getLogger(Genome.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}
}
