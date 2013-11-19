package de.hansinator.fun.jgp.life;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public abstract class Organism<E extends World> implements Comparable<Organism<E>>, Runnable, ExecutionUnit
{

	protected static final Random rnd = Settings.newRandomSource();

	protected SensorInput[] inputs = SensorInput.emptySensorInputArray;

	protected ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	private final OrganismGene<E> genome;

	public final FitnessEvaluator fitnessEvaluator;

	@SuppressWarnings("unchecked")
	private IOUnit<E>[] ioUnits = IOUnit.emptyIOUnitArray;

	private CountDownLatch cb;

	private int inputCount;
	
	public E world;

	public Organism(OrganismGene<E> genome, FitnessEvaluator evaluator)
	{
		this.genome = genome;
		this.fitnessEvaluator = evaluator;
		evaluator.attach(this);
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

	/**
	 * Execute the program
	 */
	@Override
	public abstract void execute();

	//XXX i need some computation graph to replace this call sequence
	private void live()
	{
		// prepare sensor readings
		for (IOUnit<E> u : ioUnits)
			u.sampleInputs();

		execute();

		// apply outputs (move motor etc)
		for (IOUnit<E> u : ioUnits)
			u.applyOutputs();
	}

	public int getFitness()
	{
		return fitnessEvaluator.getFitness();
	}

	public OrganismGene<E> getGenome()
	{
		return genome;
	}

	@Override
	public int compareTo(Organism<E> o)
	{
		return new Integer(this.getFitness()).compareTo(o.getFitness());
	}

	/*
	 *sometime later io units may be generalized to be attached to an organism/evaluation(sub)state
	 */
	public void setIOUnits(IOUnit<E>[] ioUnits)
	{
		int x;
		this.ioUnits = ioUnits;

		// count input ports
		for(x = 0, inputCount = 0; x < ioUnits.length; x++)
			inputCount += ioUnits[x].getInputs().length;
	}

	public void addToWorld(E world)
	{
		this.world = world;
		
		// attach bodies to world state
		for (int x = 0; x < ioUnits.length; x++)
			ioUnits[x].attachEvaluationState(world);
	}


	public IOUnit[] getIOUnits()
	{
		return ioUnits;
	}

	@Override
	public void setInputs(SensorInput[] inputs)
	{
		this.inputs = inputs;
	}

	@Override
	public void setOutputs(ActorOutput[] outputs)
	{
		this.outputs = outputs;
	}

	@Override
	public int getInputCount()
	{
		return inputs.length;
	}
}
