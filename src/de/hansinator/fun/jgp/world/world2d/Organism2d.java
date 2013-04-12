package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.io.IOException;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

/*
 * TODO: Create a loopback sense that represents the differential (ableitung)
 * of an output. Also create an integrator. This should ease temporal
 * memory functions.
 */
/**
 * 
 * @author hansinator
 */
public class Organism2d extends BaseOrganism
{

	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	public final BaseMachine vm;

	public final Body2d[] bodies;

	private final SensorInput[] inputs;

	private final ActorOutput[] outputs;

	private int food;

	/*
	 * TODO: make body composition (senses, actors) genome-defined
	 */
	public Organism2d(Genome genome, BaseMachine brain, int numBodies, int numInputs, int numOutputs)
			throws IOException
	{
		super(genome);
		this.food = 0;
		this.vm = brain;
		this.bodies = new Body2d[numBodies];
		this.inputs = new SensorInput[numInputs];
		this.outputs = new ActorOutput[numOutputs];
	}

	public void addToWorld(World2d world)
	{
		int i = 0, o = 0;

		// init bodies and grab inputs
		for (int x = 0; x < bodies.length; x++)
		{
			// create body
			bodies[x] = genome.synthesizeBody(this, world);

			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;

			bodies[x].x = rnd.nextInt(world.worldWidth);
			bodies[x].y = rnd.nextInt(world.worldHeight);
			bodies[x].dir = rnd.nextDouble() * 2 * Math.PI;
		}
	}

	@Override
	public void live()
	{
		int reg = 0;

		// calculate food stuff for body (prepare sensors..)
		for (Body2d b : bodies)
			b.prepareInputs();

		// write input registers
		for (SensorInput in : inputs)
			vm.regs[reg++] = in.get();

		vm.run();

		// write output values
		for (ActorOutput out : outputs)
			out.set(vm.regs[reg++]);

		// apply outputs (move motor etc)
		for (Body2d b : bodies)
			b.processOutputs();
	}

	@Override
	public int getFitness()
	{
		return food;
	}

	public void incFood()
	{
		food++;
	}

	public void draw(Graphics g)
	{
		for (Body2d b : bodies)
			b.draw(g);
	}

}
