package de.hansinator.fun.jgp.world.world2d.senses;

import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.Part;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;

/**
 * 
 * @author hansinator
 */
public class WallSense implements SensorInput, Part
{

	private final double worldWidth, worldHeight;

	private final Body2d body;

	SensorInput[] inputs = { this };

	private int lastSenseVal = 0;

	public WallSense(Body2d body, World world)
	{
		this.worldWidth = Math.floor(world.getWidth());
		this.worldHeight = Math.floor(world.getHeight());
		this.body = body;
	}

	public int sense()
	{
		double dir = body.dir, temp = 0.0;
		// clip to 2*PI range
		dir = dir - ((double) Math.round(dir / (2 * Math.PI)) * 2 * Math.PI);

		if ((body.x < 0) || (body.x >= worldWidth))
		{
			// TODO: fix abs stuff
			temp = Math.min(Math.abs(2 * Math.PI - dir), Math.abs(Math.PI - dir));
			if ((body.y < 0) || (body.y >= worldHeight))
				temp = Math.min(temp, Math.min(Math.abs(0.5 * Math.PI - dir), Math.abs(1.5 * Math.PI - dir)));
		} else if ((body.y < 0) || (body.y >= worldHeight))
			temp = Math.min(Math.abs(0.5 * Math.PI - dir), Math.abs(1.5 * Math.PI - dir));

		lastSenseVal = (int) Math.round(temp * Organism2d.intScaleFactor);
		return lastSenseVal;
	}

	@Override
	public int get()
	{
		return lastSenseVal;
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return ActorOutput.emptyActorOutputArray;
	}

	@Override
	public void prepareInputs()
	{
	}

	@Override
	public void processOutputs()
	{
		// pickup wallsense in process outputs before coordinates are clipped
		sense();
	}
}
