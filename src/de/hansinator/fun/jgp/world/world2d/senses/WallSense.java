package de.hansinator.fun.jgp.world.world2d.senses;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author hansinator
 */
public class WallSense implements SensorInput, BodyPart<World2d>
{

	private double worldWidth, worldHeight;

	private final Body2d body;

	SensorInput[] inputs = { this };

	private int lastSenseVal = 0;

	public WallSense(Body2d body)
	{
		this.body = body;
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
	public void sampleInputs()
	{
	}

	@Override
	public void applyOutputs()
	{
		// pickup wallsense in applyOutputs before coordinates are clipped
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
	}

	@Override
	public void attachEvaluationState(World2d world)
	{
		this.worldWidth = Math.floor(world.getWidth());
		this.worldHeight = Math.floor(world.getHeight());
	}
}
