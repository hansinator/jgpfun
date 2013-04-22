/*
 */

package de.hansinator.fun.jgp.simulation;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.genetics.crossover.CrossoverOperator;
import de.hansinator.fun.jgp.genetics.crossover.OffsetTwoPointCrossover;
import de.hansinator.fun.jgp.genetics.selection.SelectionStrategy;
import de.hansinator.fun.jgp.genetics.selection.TournamentSelector;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.Part;
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
	public AntGenome randomGenome()
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

	public class RadarAntGene implements AntGenome.Gene
	{
		@Override
		public Body2d createBody(Organism2d organism)
		{
			// create body and attach parts
			Body2d body = new AntBody(organism);
			final Part[] parts = new Part[] { new RadarSense(body), body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body)};
			body.setParts(parts);
			return body;
		}
	}

	public class FoodFinderAntGene implements AntGenome.Gene
	{
		@Override
		public Body2d createBody(Organism2d organism)
		{
			// create body and attach parts
			AntBody body = new AntBody(organism);
			final Part[] parts = new Part[] { body.locator, body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body) };
			body.setParts(parts);
			return body;
		}
	}
}
