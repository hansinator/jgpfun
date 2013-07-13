package de.hansinator.fun.jgp.simulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.gui.InfoPanel;
import de.hansinator.fun.jgp.gui.MainView;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.OrganismGene;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public class WorldSimulation
{

	// todo: have world object automatically add themselves to a legend that can
	// be drawn onto the screen (bottom?)
	// todo: in a later simulation creation dialogue, have categories for
	// "simple" stuff (bodiss, sesses) and more custom stuff.. a bit like the
	// clonk menu?!

	// (it is questionable if this must be included in propertiers... it's fine
	// if it's hardcoded for a while or two!)
	private int fpsMax = 70;

	// (it is questionable if this must be included in propertiers... it's fine
	// if it's hardcoded for a while or two!)
	private int roundsMod = 800;

	private volatile boolean running = true;

	private volatile boolean paused = false;

	private volatile boolean slowMode = false;

	private final Object runLock = new Object();

	private final ThreadPoolExecutor pool;

	public final World world;

	public static final int ROUNDS_PER_GENERATION = 4000;

	//XXX distinguish only between generational and continuous simulation, not world and mona lisa; mona lisa needs to be implemented by a scenario only
	public WorldSimulation(World world)
	{
		this.world = world;
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void initialize()
	{
		synchronized (runLock)
		{
			world.resetState();
			running = true;
			paused = false;
		}
	}

	/*
	 * FIXME: add events to the simulation, so that a main view can draw upon an
	 * event
	 * 
	 * re-think generation runtime stat calculation to be better suited for
	 * re-entrance
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OrganismGene[] evaluate(Simulator simulator, OrganismGene[] generation, MainView mainView, InfoPanel infoPanel)
	{
		long start = System.currentTimeMillis();
		long lastStatTime = start;
		int lastStatRound = 0;
		Organism[] organisms = new Organism[generation.length];
		FitnessEvaluator[] evaluators = new FitnessEvaluator[generation.length];

		// synthesize organisms
		for (int i = 0; i < generation.length; i++)
		{
			organisms[i] = generation[i].express(world);
		}

		synchronized (runLock)
		{
			for (int i = 0; running && (i < ROUNDS_PER_GENERATION); i++)
			{

				while (paused)
					Thread.yield();

				singleStep(organisms, evaluators);

				// calc stats and draw stuff
				// TODO: try to decouple this from pure generation running
				if (slowMode || (i % roundsMod) == 0)
				{
					final long time = System.currentTimeMillis() - lastStatTime;
					lastStatTime = System.currentTimeMillis();
					final int rps = time > 0 ? (int) (((i - lastStatRound) * 1000) / time) : 1;
					final int progress = (i * 100) / ROUNDS_PER_GENERATION;
					lastStatRound = i;

					// update views
					infoPanel.updateInfo(rps, progress);
					mainView.drawStuff(rps, progress);
					mainView.repaint();

					if (slowMode && (time < (1000 / fpsMax)))
						try
					{
							Thread.sleep((1000 / fpsMax) - time);
					} catch (InterruptedException ex)
					{
						Logger.getLogger(WorldSimulation.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}

		// simulation statistics
		System.out.println("");
		System.out.println("RPS: " + (ROUNDS_PER_GENERATION * 1000) / (System.currentTimeMillis() - start));

		// prepare world for next generation
		world.resetState();

		// update and return evaluated generation
		for(Organism o : organisms)
		{
			OrganismGene g = o.getGenome();
			g.setFitness(o.getFitness());
			g.setExonSize(o.getProgramSize());
		}

		return generation;
	}

	/**
	 * Stop the current evaluation.
	 */
	public void stop()
	{
		running = false;
	}

	/**
	 * Evaluate a single simulation round.
	 * 
	 * @param organisms
	 */
	@SuppressWarnings("rawtypes")
	private void singleStep(Organism[] organisms, FitnessEvaluator[] evaluators)
	{
		final CountDownLatch cb = new CountDownLatch(organisms.length);

		// evaluate each organism
		for (final Organism organism : organisms)
			organism.evaluate(cb, pool);

		// wait for all organisms to finish
		try
		{
			cb.await();
		} catch (InterruptedException ex)
		{
			Logger.getLogger(WorldSimulation.class.getName()).log(Level.SEVERE, null, ex);
		}

		// run world
		world.animate();
	}

	public boolean isSlowMode()
	{
		return slowMode;
	}

	public void setSlowMode(boolean slowMode)
	{
		this.slowMode = slowMode;
	}

	public int getRoundsMod()
	{
		return roundsMod;
	}

	public void setRoundsMod(int roundsMod)
	{
		this.roundsMod = roundsMod == 0 ? 1 : roundsMod;
	}

	public int getFps()
	{
		return fpsMax;
	}

	public void setFps(int fpsMax)
	{
		this.fpsMax = fpsMax == 0 ? 1 : fpsMax;
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	public boolean isPaused()
	{
		return paused;
	}
}
