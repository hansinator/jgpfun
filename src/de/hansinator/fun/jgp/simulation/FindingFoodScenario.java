/*
 */

package de.hansinator.fun.jgp.simulation;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import org.jbox2d.dynamics.Body;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvaluationStrategy;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.Genome.GenomeEvaluator;
import de.hansinator.fun.jgp.genetics.Genome.GenomeMutation;
import de.hansinator.fun.jgp.gui.BottomPanel;
import de.hansinator.fun.jgp.gui.EvoStats;
import de.hansinator.fun.jgp.gui.StatisticsHistoryPanel;
import de.hansinator.fun.jgp.gui.WorldSimulationView;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.CollisionListener;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.LinearVelocitySense;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;
import de.hansinator.fun.jgp.world.world2d.senses.OrientationSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
public class FindingFoodScenario implements Scenario<Genome>
{
	private static final int progSize = Settings.getInt("progSize");
	
	private static final int worldWidth = Settings.getInt("worldWidth");
	
	private static final int worldHeight = Settings.getInt("worldHeight");
	
	private static final int maxMutations = Settings.getInt("maxMutations");
	
	private static final double tournamentSelectionProbability = Settings.getDouble("tournamentSelectionProbability");
	
	private static final double wallTouchPenalty = Settings.getDouble("wallTouchPenalty");
	
	private final AntFactory antFactory;
	
	private final SelectionStrategy<Object> selectionStrategy;
	
	// this is our workhorse component for the moment.
	// most computation happens during evaluation for our scenario, in contrast
	// to the watchmaker examples where the evaluation is just a tiny fraction
	// of the computational load. we need to save a reference to the strategy
	// to pass it to the UI components for fine grained control over its
	// execution (such as pausing, slow/fast mode, etc). in watchmaker examples
	// it is sufficient to render the best candidate every once a while, because
	// they compute new candidates more than once a second. here instead we'll
	// need to render the evaluation process itself multiple times - it is
	// not sufficient to show just the solution, but the process itself
	private final WorldSimulation evaluationStrategy;
	
	private final EvolutionEngine<Genome> engine;
	
	private final EvolutionaryProcess evolutionaryProcess;
	
	private final EvoStats evoStats = new EvoStats();
	
	private JPanel simulationView;
	
	public FindingFoodScenario()
	{
		antFactory = new AntFactory(progSize);
		selectionStrategy = new TournamentSelection(new Probability(tournamentSelectionProbability));
		evaluationStrategy = new WorldSimulation(new GenomeEvaluator(), new World2d(worldWidth, worldHeight, Settings.getInt("foodCount")));
		
		// create pipeline
		List<EvolutionaryOperator<Genome>> operators = new LinkedList<EvolutionaryOperator<Genome>>();
		operators.add(new GenomeMutation(maxMutations));
		EvolutionaryOperator<Genome> evolutionPipeline = new EvolutionPipeline<Genome>(operators);
		
		// setup engine
		engine = new GenerationalEvolutionEngine<Genome>(
				antFactory,
				evolutionPipeline,
				evaluationStrategy,
				selectionStrategy,
				Settings.newRandomSource());
		engine.addEvolutionObserver(evoStats);
		
		// create a process
		evolutionaryProcess = new EvolutionaryProcess(engine);
	}
	
	public JPanel getSimulationView()
	{
		if(simulationView == null)
		{
			simulationView = new JPanel();
			simulationView.setLayout(new BorderLayout());
			simulationView.add(new WorldSimulationView(evaluationStrategy), BorderLayout.CENTER);
			simulationView.add(new BottomPanel(evaluationStrategy, evoStats), BorderLayout.SOUTH);
			simulationView.add(new StatisticsHistoryPanel(evoStats.statisticsHistory), BorderLayout.EAST);
		}
		
		return simulationView;
	}
	
	@Override
	public EvolutionEngine<Genome> getEngine()
	{
		return engine;
	}

	@Override
	public EvolutionaryProcess getEvolutionaryProcess()
	{
		return evolutionaryProcess;
	}

	@Override
	public EvaluationStrategy<Genome> getEvaluationStrategy()
	{
		return evaluationStrategy;
	}
	

	@Override
	public AbstractCandidateFactory<Genome> getCandidateFactory()
	{
		return antFactory;
	}

	@Override
	public SelectionStrategy<Object> getSelectionStrategy()
	{
		return selectionStrategy;
	}
	
	@Override
	public void start()
	{
		evolutionaryProcess.start();
	}
	
	@Override
	public void stop()
	{
		evaluationStrategy.stop();
		evolutionaryProcess.stop();	
	}

	static class FoodFitnessEvaluator implements FitnessEvaluator, CollisionListener
	{
		private double fitness = 0.0;

		@Override
		public void setFitness(int fitness)
		{
			this.fitness = fitness;
		}

		@Override
		public double getFitness()
		{
			return fitness;
		}

		@Override
		public FitnessEvaluator replicate()
		{
			return new FoodFitnessEvaluator();
		}

		@Override
		public void onCollision(Body2d a, Body b)
		{
			Object userData = b.getUserData();
			if (userData == World2d.FOOD_TAG)
			{
				fitness += 1.0;
				
				// set food to eaten to prevent further eating
				b.setUserData(World2d.EATEN_TAG);
			}
			else if (userData == World2d.WALL_TAG)
			{
				if(fitness >= wallTouchPenalty)
					fitness -= wallTouchPenalty;
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void attach(ExecutionUnit organism)
		{
			for(IOUnit u : organism.getIOUnits())
				if(u instanceof Body2d)
					((Body2d)u).addCollisionListener(this);
		}
	}
	
	static class AntFactory extends AbstractCandidateFactory<Genome>
	{	
		private final int progSize;
		
		public AntFactory(int progSize)
		{
			this.progSize = progSize;
		}
		
		@Override
		public Genome generateRandomCandidate(Random rng)
		{
			Body2d.Gene bodyGene = new Body2d.Gene();
			bodyGene.addBodyPartGene(new ObjectLocator.Gene());
			//bodyGene.addBodyPartGene(new RadarSense.Gene());
			bodyGene.addBodyPartGene(new OrientationSense.Gene());
			bodyGene.addBodyPartGene(new LinearVelocitySense.Gene());
			bodyGene.addBodyPartGene(new WallSense.Gene());
			bodyGene.addBodyPartGene(new TankMotor.Gene());
			
			LGPGene organismGene = LGPGene.randomGene(rng, progSize);
			organismGene.addIOGene(bodyGene);
			
			return new Genome(organismGene, new FoodFitnessEvaluator(), new RouletteWheelSelection());
		}
		
	}
}
