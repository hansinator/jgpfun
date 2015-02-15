package de.hansinator.fun.jgp.world.world2d.actors;

import java.util.List;

import org.jbox2d.common.Vec2;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

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
			left = value / (double)Integer.MAX_VALUE;
		}
	};

	public final ActorOutput actorRight = new ActorOutput()
	{

		@Override
		public void set(int value)
		{
			right = value / (double)Integer.MAX_VALUE;
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
		// box2d physics left
		Vec2 f = body.getBody().getWorldVector(new Vec2(0.0f, (float)(-left * maxSteerForce * 100.0f)));
		Vec2 p = body.getBody().getWorldPoint(body.getBody().getLocalCenter().add(new Vec2(-2.0f, 2.0f)));
		body.getBody().applyForce(f, p);
		
		// box2d physics right
		f = body.getBody().getWorldVector(new Vec2(0.0f, (float)(-right * maxSteerForce * 100.0f)));
		p = body.getBody().getWorldPoint(body.getBody().getLocalCenter().add(new Vec2(2.0f, 2.0f)));
		body.getBody().applyForce(f, p);
	}

	@Override
	public void attachEvaluationState(Body2d world) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Gene extends IOUnit.Gene<Body2d>
	{
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
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
