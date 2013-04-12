package de.hansinator.fun.jgp.life;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.world.world2d.Organism2d;

/**
 * 
 * @author Hansinator
 */
public abstract class BaseOrganism implements Comparable<BaseOrganism>, Runnable
{

	protected static final Random rnd = new SecureRandom();

	protected final Genome genome;

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

	protected abstract void live();

	public abstract int getFitness();

	public Genome getGenome()
	{
		return genome;
	}

	@Override
	public int compareTo(BaseOrganism o)
	{
		return new Integer(this.getFitness()).compareTo(o.getFitness());
	}

}
