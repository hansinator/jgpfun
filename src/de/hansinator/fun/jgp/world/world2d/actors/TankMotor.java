package de.hansinator.fun.jgp.world.world2d.actors;

import java.util.List;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.Simulator;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;
import de.hansinator.fun.jgp.world.world2d.senses.OrientationSense.Gene;

public class TankMotor implements BodyPart<Body2d>
{

	public static final double maxSteerForce = Settings.getDouble("maxSteerForce");

	public static final double maxSpeed = Settings.getDouble("maxSpeed");
	
	//make this a world2dobject in the future
	private final Body2d body;

	// cache motor outputs
	private double left, right;
	
	
	public TankMotor(Body2d body) {
		this.body = body;
	}

	public final ActorOutput actorLeft = new ActorOutput()
	{

		@Override
		public void set(int value)
		{
			left = Math.max(0, Math.min(value, 65535)) / Simulator.intScaleFactor;
		}
	};

	public final ActorOutput actorRight = new ActorOutput()
	{

		@Override
		public void set(int value)
		{
			right = Math.max(0, Math.min(value, 65535)) / Simulator.intScaleFactor;
		}
	};

	ActorOutput[] outputs = { actorLeft, actorRight };

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
		double speed;

		// find the direction
		body.dir += (right - left) * (maxSteerForce / 100.0);
		body.dir -= 2 * Math.PI
				* (body.dir < 0.0 ? Math.ceil(body.dir / (2 * Math.PI)) : (Math.floor(body.dir / (2 * Math.PI))));
		// max speed is just a tweaking parameter; don't get confused by it
		// try varying it in simulation
		speed = (right + left) / 2.0;
		body.lastSpeed = speed;
		
		// apply movement
		body.x += Math.sin(body.dir) * maxSpeed * speed / 10.0;
		body.y -= Math.cos(body.dir) * maxSpeed * speed / 10.0;
	}

	@Override
	public void attachEvaluationState(Body2d world) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Gene implements IOUnit.Gene<Body2d>
	{

		@Override
		public void mutate()
		{
		}

		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setMutationChance(int mutationChance)
		{
		}

		@Override
		public int getMutationChance()
		{
			return 0;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new TankMotor.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new TankMotor(context);
		}

		@Override
		public int getInputCount()
		{
			return 0;
		}

		@Override
		public int getOutputCount()
		{
			return 2;
		}

	}
}