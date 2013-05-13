package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.world.World;


public interface IOUnit
{
	public void attachEvaluationState(World world);

	public void sampleInputs();

	public void applyOutputs();

	public SensorInput[] getInputs();

	public ActorOutput[] getOutputs();

}
