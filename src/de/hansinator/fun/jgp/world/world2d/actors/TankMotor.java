package de.hansinator.fun.jgp.world.world2d.actors;

import java.util.List;

import org.jbox2d.common.Vec2;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.FloatGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

public class TankMotor implements BodyPart<Body2d>
{
	public static final double maxSpeed = Settings.getDouble("maxSpeed");
	
	private final Vec2 leftMotorPos;
	
	private final Vec2 rightMotorPos;
	
	//make this a world2dobject in the future
	private final Body2d body;

	// cache motor outputs
	private double left, right;
	
	
	public TankMotor(Body2d body, Vec2 leftMotorPos, Vec2 rightMotorPos) {
		this.body = body;
		this.leftMotorPos = leftMotorPos;
		this.rightMotorPos = rightMotorPos;
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
		// impulse drive physics left
		Vec2 f = body.getBody().getWorldVector(new Vec2(0.0f, (float)(-left * maxSpeed)));
		Vec2 p = body.getBody().getWorldPoint(body.getBody().getLocalCenter().add(leftMotorPos));
		body.getBody().applyLinearImpulse(f, p);
		
		// impulse drive physics right
		f = body.getBody().getWorldVector(new Vec2(0.0f, (float)(-right * maxSpeed)));
		p = body.getBody().getWorldPoint(body.getBody().getLocalCenter().add(rightMotorPos));
		body.getBody().applyLinearImpulse(f, p);
	}

	@Override
	public void attachEvaluationState(Body2d world) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Gene extends IOUnit.Gene<Body2d>
	{	
		private static float leftMotorPosX = (float)Settings.getDouble("leftMotorPosX");
		
		private static float leftMotorPosY = (float)Settings.getDouble("leftMotorPosY");
		
		private static float rightMotorPosX = (float)Settings.getDouble("rightMotorPosX");
		
		private static float rightMotorPosY = (float)Settings.getDouble("rightMotorPosY");
		
		private FloatGene motorSpacing = new FloatGene(1.0f, Settings.getInt("motorSpacingMutChance")); 
		
		Mutation[] mutations = { motorSpacing };
		
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			TankMotor.Gene gene = new TankMotor.Gene();
			gene.motorSpacing.setValue(motorSpacing.getValue());
			return gene;
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new TankMotor(context, new Vec2(leftMotorPosX * motorSpacing.getValue(), leftMotorPosY), new Vec2(rightMotorPosX * motorSpacing.getValue(), rightMotorPosY));
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

		@Override
		public Mutation[] getMutations()
		{
			return mutations;
		}
	}
}
