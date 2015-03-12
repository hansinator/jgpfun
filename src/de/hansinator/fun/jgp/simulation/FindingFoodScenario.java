/*
 */

package de.hansinator.fun.jgp.simulation;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jbox2d.dynamics.Body;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.Genome.GenomeMutation;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.RouletteWheelSelector;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.AntBody;
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
	
	private final AntFactory antFactory = new AntFactory(progSize);
	
	private final SelectionStrategy<Object> selectionStrategy = new TournamentSelection(Probability.EVENS);

	@Override
	public WorldSimulation getSimulation()
	{
		return new WorldSimulation(new World2d(worldWidth, worldHeight, Settings.getInt("foodCount")));
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
	public EvolutionaryOperator<Genome> createEvolutionPipeline()
	{
		List<EvolutionaryOperator<Genome>> operators = new LinkedList<EvolutionaryOperator<Genome>>();
		operators.add(new GenomeMutation(maxMutations));
		return new EvolutionPipeline<Genome>(operators);
	}

	static class FoodFitnessEvaluator implements FitnessEvaluator, CollisionListener
	{
		private int fitness = 0;

		@Override
		public int getFitness()
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
			if (b.getUserData() == World2d.FOOD_TAG)
			{
				fitness++;
				
				// set food to eaten to prevent further eating
				b.setUserData(World2d.EATEN_TAG);
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
			AntBody.Gene bodyGene = new AntBody.Gene();
			bodyGene.addBodyPartGene(new ObjectLocator.Gene());
			//bodyGene.addBodyPartGene(new RadarSense.Gene());
			bodyGene.addBodyPartGene(new OrientationSense.Gene());
			bodyGene.addBodyPartGene(new LinearVelocitySense.Gene());
			bodyGene.addBodyPartGene(new WallSense.Gene());
			bodyGene.addBodyPartGene(new TankMotor.Gene());
			
			LGPGene organismGene = LGPGene.randomGene(rng, progSize);
			organismGene.addIOGene(bodyGene);
			
			return new Genome(organismGene, new FoodFitnessEvaluator(), new RouletteWheelSelector());
		}
		
	}
}
