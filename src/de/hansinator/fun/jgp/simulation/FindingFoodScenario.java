/*
 */

package de.hansinator.fun.jgp.simulation;

import java.util.ArrayList;
import java.util.List;

import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.Organism;
import de.hansinator.fun.jgp.life.OrganismGene;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;
import de.hansinator.fun.jgp.world.world2d.World2dObject.CollisionListener;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
public class FindingFoodScenario implements Scenario
{
	private final int progSize = Settings.getInt("progSize");

	@Override
	public WorldSimulation getSimulation()
	{
		return new WorldSimulation(new World2d(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), Settings.getInt("foodCount")));
	}

	@Override
	public OrganismGene<World2d> randomGenome()
	{
		return new OrganismGene<World2d>(new LocatorAntGene(), LGPGene.randomGene(progSize), new FoodFitnessEvaluator());
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

	public class RadarAntGene implements IOUnit.Gene<World2d>
	{
		@SuppressWarnings("unchecked")
		@Override
		public IOUnit<World2d> express(Organism organism)
		{
			// create body and attach parts
			Body2d body = new AntBody(organism);
			final BodyPart<World2d>[] parts = new BodyPart[] { new RadarSense(body), body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body)};
			body.setParts(parts);
			return body;
		}

		@Override
		public RadarAntGene replicate()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void mutate()
		{
		}
	}

	public class LocatorAntGene implements IOUnit.Gene<World2d>
	{
		@SuppressWarnings("unchecked")
		@Override
		public IOUnit<World2d> express(Organism organism)
		{
			// create body and attach parts
			AntBody body = new AntBody(organism);
			final BodyPart<World2d>[] parts = new BodyPart[] { body.locator, body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body) };
			body.setParts(parts);
			return body;
		}

		@Override
		public LocatorAntGene replicate()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void mutate()
		{
		}
	}

	public class FoodFitnessEvaluator implements FitnessEvaluator, CollisionListener
	{
		private int fitness = 0;

		private List<Body2d> bodies;

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
		public void onCollision(World2dObject a, World2dObject b)
		{
			if (b instanceof Food)
			{
				fitness++;
				((Food) b).randomPosition();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void attach(Organism organism)
		{
			bodies = new ArrayList<Body2d>();
			for(IOUnit u : organism.getIOUnits())
				if(u instanceof Body2d)
					bodies.add((Body2d)u);
		}
	}
}
