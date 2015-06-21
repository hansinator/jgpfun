package de.hansinator.fun.jgp.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvaluationStrategy;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author hansinator
 */
public final class WorldSimulation implements EvaluationStrategy<Genome>
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
	
	private volatile int currentRound;

	private final ThreadPoolExecutor pool;

	public static final int ROUNDS_PER_GENERATION = 2000;
	
	private int rps;
	
	public final World world;
	
	private final ConcurrentHashMap<ExecutionUnit<? extends World>, Genome> organismsByGenome = new ConcurrentHashMap<ExecutionUnit<? extends World>, Genome>();
	
	private final List<SimulationViewUpdateListener> viewUpdateListeners = new ArrayList<SimulationViewUpdateListener>();

	private final FitnessEvaluator<? super Genome> fitnessEvaluator;

	
	public WorldSimulation(FitnessEvaluator<? super Genome> fitnessEvaluator, World world)
	{
        this.fitnessEvaluator = fitnessEvaluator;
		this.world = world;
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		world.resetState();
		running = true;
		paused = false;
		rps = 0;
	}


	/*
	 * TODO improve runtime statistics collection (look at epochx approach)
	 */
	@SuppressWarnings({"unchecked" })
	public List<EvaluatedCandidate<Genome>> evaluatePopulation(List<Genome> population)
	{
		long start = System.currentTimeMillis();
		long lastStatTime = start;
		int lastStatRound = 0;
		ExecutionUnit<? extends World>[] organisms = new ExecutionUnit[population.size()];
      
		// synthesize organisms for fitness evaluation
        organismsByGenome.clear();
		for (int i = 0; i < population.size(); i++)
		{
			Genome genome = population.get(i);
			de.hansinator.fun.jgp.life.FitnessEvaluator evaluator = genome.getFitnessEvaluator();
			
			//TODO move these into a Genome.synthesise function so we don't need fitnessevaluator knowledge here
			organisms[i] = genome.getRootGene().express((World2d) world);
			evaluator.attach(organisms[i]);
			evaluator.setFitness(0);
			
			// record organism-genome relationship
			organismsByGenome.put(organisms[i], genome);
		}
		
		// evaluate organisms in a world
		for (currentRound = 0; running && (currentRound < ROUNDS_PER_GENERATION); currentRound++)
		{
			while (paused)
				Thread.yield();

			singleStep(organisms);

			// calc stats and draw stuff
			// TODO: try to decouple this from pure generation running
			if (slowMode || (currentRound % roundsMod) == 0)
			{
				final long time = System.currentTimeMillis() - lastStatTime;
				lastStatTime = System.currentTimeMillis();
				this.rps = time > 0 ? (int) (((currentRound - lastStatRound) * 1000) / time) : 1;
				lastStatRound = currentRound;

				// update views
				updateSimulationViews();

				// slow down things artificially
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

		// print simulation statistics
		System.out.println("");
		System.out.println("RPS: " + (ROUNDS_PER_GENERATION * 1000) / (System.currentTimeMillis() - start));
		
		// reset world
		world.resetState();
		
		// assign fitness scores
		List<EvaluatedCandidate<Genome>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<Genome>>(population.size());
		for (ExecutionUnit<? extends World> organism : organisms)
		{
			Genome genome = organismsByGenome.get(organism);
            evaluatedPopulation.add(new EvaluatedCandidate<Genome>(genome, genome.getFitnessEvaluator().getFitness()));
		}
		
		return evaluatedPopulation;
	}


	/**
	 * Evaluate a single simulation round.
	 * 
	 * @param organisms
	 */
	@SuppressWarnings("rawtypes")
	private void singleStep(ExecutionUnit[] organisms)
	{
		final CountDownLatch cb = new CountDownLatch(organisms.length);

		/*
		 * Evaluate each organism non-blocking as a thread using an
		 * ExecutorService. The count-down latch will be count down when the
		 * organism is done living one round.
		 * 
		 * Use an anonymous runnable here to decouple threading logic from
		 * the unit under evaluation. It doesn't make me happy but it's not
		 * as ugly as having each organism implement runnable.
		 */
		for (final ExecutionUnit unit : organisms)
		{
			pool.execute(new Runnable() {
				
				@Override
				public void run()
				{
					try
					{
						unit.execute();
					} catch (Exception ex)
					{
						Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
					}

					cb.countDown();
				}
			});
		}

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
	
	/**
	 * Stop the current evaluation.
	 */
	public void stop()
	{
		running = false;
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
	
	public int getCurrentRound()
	{
		return currentRound;
	}
	
	public int getRPS()
	{
		return rps;
	}
	
	
	final synchronized public boolean addViewUpdateListener(SimulationViewUpdateListener listener)
	{
		return viewUpdateListeners.add(listener);
	}

	final synchronized public boolean removeViewUpdateListener(SimulationViewUpdateListener listener)
	{
		return viewUpdateListeners.remove(listener);
	}

	final void updateSimulationViews()
	{
		for(SimulationViewUpdateListener listener : viewUpdateListeners)
			listener.onViewUpdate();
	}

	public static interface SimulationViewUpdateListener
	{
		public void onViewUpdate();
	}
	
	public Map<ExecutionUnit<? extends World>, Genome> getOrganismsByGenomeMap()
	{
		return java.util.Collections.unmodifiableMap(organismsByGenome);
	}

	@Override
	public boolean isNatural()
	{
		return fitnessEvaluator.isNatural();
	}

	@Override
	public void setSingleThreaded(boolean singleThreaded)
	{
		// TODO Auto-generated method stub
		
	}
}
