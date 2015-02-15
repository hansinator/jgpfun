/*
 */

package de.hansinator.fun.jgp.simulation;

import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.RouletteWheelSelector;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;
import de.hansinator.fun.jgp.world.world2d.World2dObject.CollisionListener;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.OrientationSense;
import de.hansinator.fun.jgp.world.world2d.senses.SpeedSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
public class FindingFoodScenario implements Scenario
{
	private final int progSize = Settings.getInt("progSize");
	
	private Random rnd = Settings.newRandomSource();
	
	private final int worldWidth = Settings.getInt("worldWidth");
	
	private final int worldHeight = Settings.getInt("worldHeight");

	@Override
	public WorldSimulation getSimulation()
	{
		return new WorldSimulation(new World2d(worldWidth, worldHeight, Settings.getInt("foodCount")));
	}

	@Override
	public Genome randomGenome()
	{
		AntBody.Gene bodyGene = new AntBody.Gene(true);
		//bodyGene.addBodyPartGene(new RadarSense.Gene());
		//bodyGene.addBodyPartGene(new RadarSense.Gene());
		bodyGene.addBodyPartGene(new OrientationSense.Gene());
		bodyGene.addBodyPartGene(new SpeedSense.Gene());
		bodyGene.addBodyPartGene(new WallSense.Gene());
		bodyGene.addBodyPartGene(new TankMotor.Gene());
		
		LGPGene organismGene = LGPGene.randomGene(progSize);
		organismGene.addIOGene(bodyGene);
		
		return new Genome(organismGene, new FoodFitnessEvaluator(), new RouletteWheelSelector());
	}

	@Override
	public CrossoverOperator getCrossoverOperator()
	{
		return new OffsetTwoPointCrossover(progSize / 8);
	}

	@Override
	public SelectionStrategy getSelectionStrategy()
	{
		return new TournamentSelector(3);
	}


	public class FoodFitnessEvaluator implements FitnessEvaluator, CollisionListener
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
		public void onCollision(World2dObject a, Body b)
		{
			if (b.getUserData() == World2d.FOOD_TAG)
			{
				fitness++;
				b.setTransform(new Vec2((float)rnd.nextInt(worldWidth), (float)rnd.nextInt(worldHeight)), (float)Math.PI);
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
}
