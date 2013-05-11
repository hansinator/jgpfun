/*
 */

package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
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
	public Genome randomGenome()
	{
		return new AntGenome(new FoodFinderAntGene(), progSize);
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

	public class RadarAntGene implements Body2d.Body2dGene
	{
		@SuppressWarnings("unchecked")
		@Override
		public Body2d express(Organism2d organism)
		{
			// create body and attach parts
			Body2d body = new AntBody(organism);
			final BodyPart<World2d>[] parts = new BodyPart[] { new RadarSense(body), body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body)};
			body.setParts(parts);
			return body;
		}

		@Override
		public Gene<Body2d, Organism2d> replicate()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class FoodFinderAntGene implements Body2d.Body2dGene
	{
		@SuppressWarnings("unchecked")
		@Override
		public Body2d express(Organism2d organism)
		{
			// create body and attach parts
			AntBody body = new AntBody(organism);
			final BodyPart<World2d>[] parts = new BodyPart[] { body.locator, body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body) };
			body.setParts(parts);
			return body;
		}

		@Override
		public Gene<Body2d, Organism2d> replicate()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
