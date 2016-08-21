package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.DoubleGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

public class OrientationSense implements SensorInput, BodyPart<Body2d>
{
	private final double orientationScaleFactor;
	
	private final SensorInput[] inputs = { this };

	private Body body;
	
	private float angle;
	
	public OrientationSense(double orientationScaleFactor)
	{
		this.orientationScaleFactor = orientationScaleFactor;
	}

	@Override
	public double get()
	{
		// could also be sin
		return Math.cos(angle) * orientationScaleFactor;
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
		angle = body.getAngle();
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.body = context.getBody();
	}

	public static class Gene extends IOUnit.Gene<Body2d>
	{
		private DoubleGene orientationScaleFactor = new DoubleGene(1.0, 500);
		
		Mutation[] mutations = { orientationScaleFactor };
		
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			return new OrientationSense.Gene();
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new OrientationSense(orientationScaleFactor.getValue() * EvolutionaryProcess.intScaleFactor);
		}

		@Override
		public int getInputCount()
		{
			return 1;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

		@Override
		public Mutation[] getMutations()
		{
			return mutations;
		}
	}
}