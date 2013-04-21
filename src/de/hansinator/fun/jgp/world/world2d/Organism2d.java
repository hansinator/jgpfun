package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;
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

	public Body2d[] bodies;

	private BaseMachine vm;

	private SensorInput[] inputs = SensorInput.emptySensorInputArray;

	private ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	private int food;

	/*
	 * idea: think of inputs and outputs in term of I/O in computers (i.e. interface to real world) and regard bodies as I/O units or ports
	 * TODO: I could move the VM into baseorganism and try to write a generic live function
	 */
	public Organism2d(Genome genome)
	{
		super(genome);
		this.food = 0;
	}

	public void setBodies(Body2d[] bodies)
	{
		int i, o, x;
		this.bodies = bodies;

		// count I/O ports
		for (x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			i += bodies[x].getInputs().length;
			o += bodies[x].getOutputs().length;
		}

		// create arrays
		inputs = new SensorInput[i];
		outputs = new ActorOutput[o];

		// grab I/O ports
		for (x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}
	}

	public void addToWorld(World world)
	{
		// attach bodies to world state
		for (int x = 0; x < bodies.length; x++)
			bodies[x].addToWorld(world);
	}

	@Override
	public void live()
	{
		int reg = 0;

		// calculate food stuff for body (prepare sensors..)
		for (Body2d b : bodies)
			b.sampleInputs();

		// write input registers
		for (SensorInput in : inputs)
			vm.regs[reg++] = in.get();

		vm.run();

		// write output values
		for (ActorOutput out : outputs)
			out.set(vm.regs[reg++]);

		// apply outputs (move motor etc)
		for (Body2d b : bodies)
			b.applyOutputs();
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

	public int getInputCount()
	{
		return inputs.length;
	}

	public void setVM(BaseMachine vm)
	{
		this.vm = vm;
	}

}
