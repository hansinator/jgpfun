package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.DoubleGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

public class LinearVelocitySense implements SensorInput, BodyPart<Body2d>
{
	private final double linearVelocityScaleFactor;
	
	private final SensorInput[] inputs = { this };

	private final Body2d body;

	public LinearVelocitySense(Body2d body2d, double linearVelocityScaleFactor)
	{
		this.body = body2d;
		this.linearVelocityScaleFactor = linearVelocityScaleFactor;
	}

	@Override
	public double get()
	{
		return body.getBody().getLinearVelocity().length() * linearVelocityScaleFactor;
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
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
	}
	
	public static class Gene extends IOUnit.Gene<Body2d>
	{
		private DoubleGene linearVelocityScaleFactor = new DoubleGene(1.0, 500);
		
		Mutation[] mutations = { linearVelocityScaleFactor };
		
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			LinearVelocitySense.Gene gene = new LinearVelocitySense.Gene();
			gene.linearVelocityScaleFactor.setValue(linearVelocityScaleFactor.getValue());
			return gene;
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new LinearVelocitySense(context, linearVelocityScaleFactor.getValue() * EvolutionaryProcess.intScaleFactor);
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