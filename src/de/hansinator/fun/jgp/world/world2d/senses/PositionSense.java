package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

/**
 * 
 * @author hansinator
 */
public class PositionSense implements BodyPart<Body2d>
{

	private Body body;
	
	private Vec2 position;

	public final SensorInput senseX = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float)(position.x * EvolutionaryProcess.intScaleFactor));
		}
	};

	public final SensorInput senseY = new SensorInput()
	{

		@Override
		public int get()
		{
			return Math.round((float)(position.y * EvolutionaryProcess.intScaleFactor));
		}
	};

	SensorInput[] inputs = { senseX, senseY };

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
		position = body.getPosition();
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void attachEvaluationState(Body2d body)
	{
		this.body = body.getBody();
	}
	
	
	public class Gene extends IOUnit.Gene<Body2d>
	{
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new PositionSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new PositionSense();
		}

		@Override
		public int getInputCount()
		{
			return 2;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

	}
}
