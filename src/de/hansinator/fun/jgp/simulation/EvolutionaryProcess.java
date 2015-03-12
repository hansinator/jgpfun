package de.hansinator.fun.jgp.simulation;

import java.util.Random;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.UserAbort;

import de.hansinator.fun.jgp.genetics.GenealogyTree;
import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.Genome.GenomeEvaluator;
import de.hansinator.fun.jgp.util.Settings;

public class EvolutionaryProcess
{
	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	private final WorldSimulation simulation;

	private final GenealogyTree genealogyTree;

	private final Scenario<Genome> scenario;

	private final Random rng;

	private final int popSize = Settings.getInt("popSize");
	
	private final int eliteCount = Settings.getInt("eliteCount");
	
	private final UserAbort abort = new UserAbort();

	private EvolutionEngine<Genome> engine;
	
	private EvolutionObserver<? super Genome> observer;

	public EvolutionaryProcess(Scenario<Genome> scenario, EvolutionObserver<? super Genome> observer)
	{
		this.scenario = scenario;
		this.observer = observer;
		simulation = scenario.getSimulation();
		genealogyTree = new GenealogyTree();
		rng = Settings.newRandomSource();
	}

	/**
	 * Start a stopped simulation without resetting it.
	 * 
	 * TODO: do not start a new simulation when one is running
	 * 
	 * @param mainView
	 * @param infoPanel
	 */
	synchronized public void start()
	{
		abort.reset();
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				EvolutionaryProcess.this.run();
			}
		}, "EvolutionThread" + this).start();
	}

	private void run()
	{
		long startTime = System.currentTimeMillis();

		// setup simulation
		simulation.initialize();
		System.out.println("Start time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(startTime));
		
		// setup engine
        FitnessEvaluator<Genome> evaluator = new CachingFitnessEvaluator<Genome>(new GenomeEvaluator());
        engine = new GenerationalEvolutionEngine<Genome>(scenario.getCandidateFactory(), scenario.createEvolutionPipeline(), evaluator, scenario.getSelectionStrategy(), rng);
        engine.addEvolutionObserver(observer);
        //engine.addEvolutionObserver(monitor);

		// run simulation
        engine.evolve(popSize, eliteCount, new TerminationCondition[]{ abort });

		// print statistics
		System.out.println("\nEnd time: "
				+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));
		System.out.println("Runtime: "
				+ PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, System.currentTimeMillis())));
	}

	public void stop()
	{
		abort.abort();
		simulation.stop();
	}

	public WorldSimulation getSimulation()
	{
		return simulation;
	}
}
