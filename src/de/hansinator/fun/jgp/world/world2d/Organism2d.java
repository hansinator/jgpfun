package de.hansinator.fun.jgp.world.world2d;

import de.hansinator.fun.jgp.genetics.AntGenome;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.util.Settings;


/**
 * 
 * @author hansinator
 */
public class Organism2d extends BaseOrganism<World2d>
{

	public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

	private int food;

	public Organism2d(AntGenome genome)
	{
		super(genome);
		this.food = 0;
	}

	@Override
	public int getFitness()
	{
		return food;
	}

	public void incFood()
	{
		food++;
	}
}
