package de.hansinator.fun.jgp.simulation;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.UserAbort;

import de.hansinator.fun.jgp.util.Settings;

public class EvolutionaryProcess
{
	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	private final int popSize = Settings.getInt("popSize");
	
	private final int eliteCount = Settings.getInt("eliteCount");
	
	private final UserAbort abort = new UserAbort();

	private EvolutionEngine<?> engine;

	public EvolutionaryProcess(EvolutionEngine<?> engine)
	{
		this.engine = engine;
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
				long startTime = System.currentTimeMillis();
				System.out.println("Start time: "
						+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(startTime));

				// run simulation
		        engine.evolve(popSize, eliteCount, new TerminationCondition[]{ abort });

				// print statistics
				System.out.println("\nEnd time: "
						+ DateTimeFormat.fullDateTime().withZone(DateTimeZone.getDefault()).print(new Instant()));
				System.out.println("Runtime: "
						+ PeriodFormat.getDefault().print(new org.joda.time.Period(startTime, System.currentTimeMillis())));
			}
		}, "EvolutionThread" + this).start();
	}

	public void stop()
	{
		abort.abort();
	}
}
