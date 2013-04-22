package de.hansinator.fun.jgp.life;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

/**
 * 
 * @author Hansinator
 */
public abstract class BaseOrganism implements Comparable<BaseOrganism>, Runnable
{

	protected static final Random rnd = Settings.newRandomSource();

	protected final AntGenome genome;

	private SensorInput[] inputs = SensorInput.emptySensorInputArray;

	private ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;

	private BaseMachine vm;

	private CountDownLatch cb;

	public BaseOrganism(AntGenome genome)
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
			Logger.getLogger(Organism2d.class.getName()).log(Level.SEVERE, null, ex);
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

	public abstract void sampleInputs();

	public abstract void applyOutputs();

	public abstract int getFitness();

	public AntGenome getGenome()
	{
		return genome;
	}

	@Override
	public int compareTo(BaseOrganism o)
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
}
