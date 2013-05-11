package de.hansinator.fun.jgp.life;

import java.awt.Graphics;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.ActorOutput;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.SensorInput;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author Hansinator
 */
/*
 * TODO: Create a loopback sense that represents the differential (ableitung)
 * of an output. Also create an integrator. This should ease temporal
 * memory functions.
 */
/*
 * idea: think of inputs and outputs in term of I/O in computers (i.e. interface to real world) and regard bodies as I/O units or ports
 * TODO: I could move the VM into baseorganism and try to write a generic live function
 */
public abstract class BaseOrganism<E extends World> implements Comparable<BaseOrganism<E>>, Runnable
{

	protected static final Random rnd = Settings.newRandomSource();

	protected final Genome genome;

	private SensorInput[] inputs = SensorInput.emptySensorInputArray;

	private ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	@SuppressWarnings("unchecked")
	private BodyPart<E>[] bodyParts = BodyPart.emptyBodyPartArray;

	@SuppressWarnings("unchecked")
	protected BodyPart.DrawablePart<E>[] drawableParts = BodyPart.DrawablePart.emptyDrawablePartArray;

	private BaseMachine vm;

	private CountDownLatch cb;

	public BaseOrganism(Genome genome)
	{
		this.genome = genome;
	}

	@Override
	public void run()
	{
		// find closest food
		try
		{
			live();
		} catch (Exception ex)
		{
			Logger.getLogger(BaseOrganism.class.getName()).log(Level.SEVERE, null, ex);
		}

		cb.countDown();
	}

	/**
	 * Evaluate this organism non-blocking as a thread using an ExecutorService.
	 * The count-down latch will be count down when the organism is done living
	 * one round.
	 * 
	 * XXX: Maybe the use of the CountDownLatch can be avoided by using futures.
	 * 
	 * @param cb
	 * @param executor
	 */
	public void evaluate(CountDownLatch cb, ExecutorService executor)
	{
		this.cb = cb;
		executor.execute(this);
	}

	/**
	 * Evaluate this organism.
	 */
	public void evaluate()
	{
		live();
	}

	private void live()
	{
		int reg = 0;

		sampleInputs();

		// write input registers
		for (SensorInput in : inputs)
			vm.regs[reg++] = in.get();

		vm.run();

		// write output values
		for (ActorOutput out : outputs)
			out.set(vm.regs[reg++]);

		applyOutputs();
	}

	public abstract int getFitness();

	public Genome getGenome()
	{
		return genome;
	}

	@Override
	public int compareTo(BaseOrganism<E> o)
	{
		return new Integer(this.getFitness()).compareTo(o.getFitness());
	}

	public void setVM(BaseMachine vm)
	{
		this.vm = vm;
	}

	public int getInputCount()
	{
		return inputs.length;
	}

	public int getProgramSize()
	{
		return vm.getProgramSize();
	}

	public void setInputs(SensorInput[] inputs)
	{
		if(inputs == null)
			this.inputs = SensorInput.emptySensorInputArray;
		else
			this.inputs = inputs;
	}

	public void setOutputs(ActorOutput[] outputs)
	{
		if(outputs == null)
			this.outputs = ActorOutput.emptyActorOutputArray;
		else
			this.outputs = outputs;
	}

	/*
	 *sometime later bodyparts may also be generalized to some kind of i/o bundles/modules attached to an organism/evaluation(sub)state
	 */
	@SuppressWarnings("unchecked")
	public void setBodyParts(BodyPart<E>[] parts)
	{
		int i, o, d, x;

		this.bodyParts = parts;

		// count I/O ports and drawable parts
		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			i += parts[x].getInputs().length;
			o += parts[x].getOutputs().length;
			if (parts[x] instanceof BodyPart.DrawablePart)
				d++;
		}

		// create arrays
		SensorInput[] inputs = new SensorInput[i];
		ActorOutput[] outputs = new ActorOutput[o];
		drawableParts = new BodyPart.DrawablePart[d];

		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			// collect inputs
			for (SensorInput in : parts[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : parts[x].getOutputs())
				outputs[o++] = out;

			// collect drawable parts
			if (parts[x] instanceof BodyPart.DrawablePart)
				drawableParts[d++]= (BodyPart.DrawablePart<E>) parts[x];
		}

		// attach I/O
		setInputs(inputs);
		setOutputs(outputs);
	}

	public void addToWorld(E world)
	{
		// attach bodies to world state
		for (int x = 0; x < bodyParts.length; x++)
			bodyParts[x].addToWorld(world);
	}

	public void sampleInputs()
	{
		// calculate food stuff for body (prepare sensors..)
		for (BodyPart<E> b : bodyParts)
			b.sampleInputs();
	}

	public void applyOutputs()
	{
		// apply outputs (move motor etc)
		for (BodyPart<E> b : bodyParts)
			b.applyOutputs();
	}

	public void draw(Graphics g)
	{
		for (BodyPart.DrawablePart<E> part : drawableParts)
			part.draw(g);
	}
}
