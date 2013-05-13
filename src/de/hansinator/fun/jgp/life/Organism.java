package de.hansinator.fun.jgp.life;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.util.Settings;
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
public class Organism<E extends World> implements Comparable<Organism<E>>, Runnable
{

	protected static final Random rnd = Settings.newRandomSource();

	protected final Genome genome;

	private SensorInput[] inputs = SensorInput.emptySensorInputArray;

	private ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	@SuppressWarnings("unchecked")
	private IOUnit<E>[] ioUnits = IOUnit.emptyIOUnitArray;

	private BaseMachine vm;

	private CountDownLatch cb;

	private int fitness;

	public Organism(Genome genome)
	{
		this.genome = genome;
		this.fitness = 0;
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
			Logger.getLogger(Organism.class.getName()).log(Level.SEVERE, null, ex);
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

		// prepare sensor readings
		for (IOUnit<E> u : ioUnits)
			u.sampleInputs();

		// write input registers
		for (SensorInput in : inputs)
			vm.regs[reg++] = in.get();

		vm.run();

		// write output values
		for (ActorOutput out : outputs)
			out.set(vm.regs[reg++]);

		// apply outputs (move motor etc)
		for (IOUnit<E> u : ioUnits)
			u.applyOutputs();
	}

	public int getFitness()
	{
		return fitness;
	}

	public void incFitness()
	{
		fitness++;
	}

	public Genome getGenome()
	{
		return genome;
	}

	@Override
	public int compareTo(Organism<E> o)
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

	/*
	 *sometime later io units may be generalized to be attached to an organism/evaluation(sub)state
	 */
	public void setIOUnits(IOUnit<E>[] ioUnits)
	{
		int i, o, x;

		this.ioUnits = ioUnits;

		// count I/O ports and drawable parts
		for(x = 0, i = 0, o = 0; x < ioUnits.length; x++)
		{
			i += ioUnits[x].getInputs().length;
			o += ioUnits[x].getOutputs().length;
		}

		// create arrays
		inputs = (i==0)?SensorInput.emptySensorInputArray:new SensorInput[i];
		outputs = (o==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[o];

		// attach I/O
		for(x = 0, i = 0, o = 0; x < ioUnits.length; x++)
		{
			// collect inputs
			for (SensorInput in : ioUnits[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : ioUnits[x].getOutputs())
				outputs[o++] = out;
		}
	}

	public void addToWorld(E world)
	{
		// attach bodies to world state
		for (int x = 0; x < ioUnits.length; x++)
			ioUnits[x].attachEvaluationState(world);
	}
}
