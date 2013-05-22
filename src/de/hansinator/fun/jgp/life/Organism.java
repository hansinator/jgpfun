package de.hansinator.fun.jgp.life;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
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

	@SuppressWarnings("unchecked")
	private IOUnit<E>[] ioUnits = IOUnit.emptyIOUnitArray;

	private BaseMachine vm;

	private CountDownLatch cb;

	private int fitness;

	private int inputCount;

	public Organism(Genome genome)
	{
		this.genome = genome;
		this.fitness = 0;
	}

	public static <E extends World> Organism<E> synthesize(Genome genome, E world)
	{
		int i, o, x;

		// create organism
		Organism<E> organism = new Organism<E>(genome);

		// create and attach body
		BodyPart<E>[] bodies = new BodyPart[] { genome.getBodyGene().express(organism) };
		organism.setIOUnits(bodies);

		// count I/O ports
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			i += bodies[x].getInputs().length;
			o += bodies[x].getOutputs().length;
		}

		// create I/O arrays
		SensorInput[] inputs = (i==0)?SensorInput.emptySensorInputArray:new SensorInput[i];
		ActorOutput[] outputs = (o==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[o];

		// collect I/O
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}

		// create brain
		BaseMachine vm = genome.getBrainGene().express(organism);
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// attach inputs and outputs to brain
		vm.setInputs(inputs);
		vm.setOutputs(outputs);

		// attach brain
		organism.setVM(vm);

		//attach to evaluation state
		organism.addToWorld(world);

		// return assembled organism
		return organism;
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
		// prepare sensor readings
		for (IOUnit<E> u : ioUnits)
			u.sampleInputs();

		vm.run();

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
		return inputCount;
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
		int x;
		this.ioUnits = ioUnits;

		// count input ports
		for(x = 0, inputCount = 0; x < ioUnits.length; x++)
			inputCount += ioUnits[x].getInputs().length;
	}

	public void addToWorld(E world)
	{
		// attach bodies to world state
		for (int x = 0; x < ioUnits.length; x++)
			ioUnits[x].attachEvaluationState(world);
	}
}
