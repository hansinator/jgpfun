package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;

import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

public abstract class Body2d extends World2dObject {

	protected final SensorInput[] inputs;

	protected final ActorOutput[] outputs;

	public double lastSpeed = 0.0;

	public double dir;

	public volatile boolean tagged = false;

	public Body2d(double x, double y, double dir, SensorInput[] inputs,
			ActorOutput[] outputs) {
		// TODO: fix null pointer
		super(null, x, y);
		this.dir = dir;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public SensorInput[] getInputs() {
		return inputs;
	}

	public ActorOutput[] getOutputs() {
		return outputs;
	}

	public abstract void prepareInputs();

	public abstract void applyOutputs();

	public abstract void postRoundTrigger();

	//XXX: fix this by better design
	//public abstract int getNumOutputs();
	//public abstract int getNumInputs();

	@Override
	public abstract void draw(Graphics g);

	protected class OrientationSense implements SensorInput {

		@Override
		public int get() {
			// could also be sin
			return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
		}

	}

	protected class SpeedSense implements SensorInput {

		@Override
		public int get() {
			return (int) (lastSpeed * Organism2d.intScaleFactor);
		}

	}
}
