package de.hansinator.fun.jgp.simulation;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.gui.InfoPanel;
import de.hansinator.fun.jgp.gui.MainView;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author hansinator
 */
public class Simulation
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

	private MainView mainView;

	private InfoPanel infoPanel;

	public Simulation()
	{
		world = new World2d(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), Settings.getInt("foodCount"));
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void initialize(MainView mainView, InfoPanel infoPanel)
	{
		synchronized (runLock)
		{
			this.mainView = mainView;
			this.infoPanel = infoPanel;
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
	public List<BaseOrganism> evaluate(Simulator simulator, List<BaseOrganism> organisms)
	{
		long start = System.currentTimeMillis();
		long lastStatTime = start;
		int lastStatRound = 0;

		// put organisms into world
		world.setOrganisms(organisms);

		synchronized (runLock)
		{
			for (int i = 0; running && (i < ROUNDS_PER_GENERATION); i++)
			{

				while (paused)
					Thread.yield();

				singleStep(organisms);

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
							Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
						}
				}
			}
		}

		// simulation statistics
		System.out.println("");
		System.out.println("RPS: " + (ROUNDS_PER_GENERATION * 1000) / (System.currentTimeMillis() - start));

		// prepare world for next generation
		world.resetState();

		// return evaluated generation
		return organisms;
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
	private void singleStep(List<BaseOrganism> organisms)
	{
		final CountDownLatch cb = new CountDownLatch(organisms.size());

		// evaluate each organism
		for (final BaseOrganism organism : organisms)
			organism.evaluate(cb, pool);

		// wait for all organisms to finish
		try
		{
			cb.await();
		} catch (InterruptedException ex)
		{
			Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
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

	public boolean isPausede()
	{
		return paused;
	}
}
