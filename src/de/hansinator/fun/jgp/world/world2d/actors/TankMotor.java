package de.hansinator.fun.jgp.world.world2d.actors;

import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.ActorOutput;
import de.hansinator.fun.jgp.world.SensorInput;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Organism2d;

public class TankMotor implements Motor2d
{

	public static final double maxSteerForce = Settings.getDouble("maxSteerForce");

	public static final double maxSpeed = Settings.getDouble("maxSpeed");

	private final Body2d body;

	// cache motor outputs
	private double left, right;

	public final ActorOutput actorLeft = new ActorOutput()
	{

		@Override
		public void set(int value)
		{
			left = Math.max(0, Math.min(value, 65535)) / Organism2d.intScaleFactor;
		}
	};

	public final ActorOutput actorRight = new ActorOutput()
	{

		@Override
		public void set(int value)
		{
			right = Math.max(0, Math.min(value, 65535)) / Organism2d.intScaleFactor;
		}
	};

	ActorOutput[] outputs = { actorLeft, actorRight };

	public TankMotor(Body2d body)
	{
		this.body = body;
	}

	// compute movement here
	@Override
	public void move()
	{
		double speed;

		// find the direction
		body.dir += (right - left) * (maxSteerForce / 100.0);
		body.dir -= 2 * Math.PI
				* (body.dir < 0.0 ? Math.ceil(body.dir / (2 * Math.PI)) : (Math.floor(body.dir / (2 * Math.PI))));
		// max speed is just a tweaking parameter; don't get confused by it
		// try varying it in simulation
		speed = (right + left) / 2.0;
		body.lastSpeed = speed;
		body.x += Math.sin(body.dir) * maxSpeed * speed / 10.0;
		body.y -= Math.cos(body.dir) * maxSpeed * speed / 10.0;
	}

	@Override
	public SensorInput[] getInputs()
	{
		return SensorInput.emptySensorInputArray;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return outputs;
	}

	@Override
	public void sampleInputs()
	{
	}

	@Override
	public void applyOutputs()
	{
		move();
	}

	@Override
	public void addToWorld(World world)
	{
		// TODO Auto-generated method stub
		
	}
}
