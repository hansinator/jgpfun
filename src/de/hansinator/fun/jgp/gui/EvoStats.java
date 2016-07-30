package de.hansinator.fun.jgp.gui;

import org.jfree.data.xy.XYSeries;
import org.joda.time.format.PeriodFormat;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.gui.StatisticsHistoryTable.StatisticsHistoryModel;

public class EvoStats implements EvolutionObserver<Genome>
{
	public final StatisticsHistoryModel statisticsHistory = new StatisticsHistoryModel();

	public final XYSeries fitnessChartData = new XYSeries("fitness");

	public final XYSeries genomeSizeChartData = new XYSeries("prg size");

	public final XYSeries realGenomeSizeChartData = new XYSeries("real prg size");
	
	private int lastEvaluationCount = 0, generation = 0;
	
	long lastStatsTime, evaluationsPerMinuteAverage = 0, evaluationsPerMinuteCount = 0;
	
	public EvoStats()
	{
		fitnessChartData.setMaximumItemCount(500);
		genomeSizeChartData.setMaximumItemCount(500);
		realGenomeSizeChartData.setMaximumItemCount(500);
		//XXX this is a crude solution
		lastStatsTime = System.currentTimeMillis();
	}

	public void reset()
	{
		fitnessChartData.clear();
		genomeSizeChartData.clear();
		realGenomeSizeChartData.clear();
		statisticsHistory.clear();
		//XXX this is a crude solution
		lastStatsTime = System.currentTimeMillis();
	}
	
	@Override
    public void populationUpdate(PopulationData<? extends Genome> data)
    {
		generation = data.getGenerationNumber();

		// population statistics
		{
    		int avgProgSize = 0, avgRealProgSize = 0, totalFit = 0;
    		int popSize = data.getPopulationSize();

    		for (EvaluatedCandidate<? extends Genome> e : data.getEvaluatedPopulation())
    		{
    			Genome g = e.getCandidate();
    			avgProgSize += g.getRootGene().getSize();
    			avgRealProgSize += g.getRootGene().getExonSize();
    			totalFit += g.getFitnessEvaluator().getFitness();
    		}
    		avgProgSize /= popSize;
    		avgRealProgSize /= popSize;
    		
    		
    		statisticsHistory.appendEntry(generation, data.getBestCandidateFitness(), data.getMeanFitness(), avgProgSize, avgRealProgSize);
    		genomeSizeChartData.add(generation, avgProgSize);
    		realGenomeSizeChartData.add(generation, avgRealProgSize);
			fitnessChartData.add(generation, data.getMeanFitness());
			System.out.println("GEN: " + generation);
		}

		// performance statistics
		{
			long now;
			
			now = System.currentTimeMillis();
			if ((now - lastStatsTime) >= 3000)
			{
				long evaluationsPerMinute = (generation - lastEvaluationCount)
						* (60000 / (now - lastStatsTime));
				evaluationsPerMinuteAverage += evaluationsPerMinute;
				evaluationsPerMinuteCount++;

				System.out.println("GPM: " + evaluationsPerMinute);
				System.out.println("Runtime: " + PeriodFormat.getDefault().print(new org.joda.time.Period(data.getElapsedTime())));
				lastEvaluationCount = generation;
				lastStatsTime = now;
			}
		}
    }
	
	public int getGenerationNumber()
	{
		return generation;
	}
}