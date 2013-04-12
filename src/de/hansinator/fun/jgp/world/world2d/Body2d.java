package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

public abstract class Body2d extends World2dObject {

	protected final SensorInput[] inputs;

	protected final ActorOutput[] outputs;
	
	protected final Organism2d organism;

	public double lastSpeed = 0.0;

	public double dir;

	public volatile boolean tagged = false;

	public Body2d(Organism2d organism, double x, double y, double dir, SensorInput[] inputs,
			ActorOutput[] outputs) {
		// TODO: fix null pointer
		super(null, x, y);
		this.organism = organism;
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
	public void draw(Graphics g) {
		final double sindir = Math.sin(dir);
		final double cosdir = Math.cos(dir);
		final double x_len_displace = 6.0 * sindir;
		final double y_len_displace = 6.0 * cosdir;
		final double x_width_displace = 4.0 * sindir;
		final double y_width_displace = 4.0 * cosdir;
		final double x_bottom = x - x_len_displace;
		final double y_bottom = y + y_len_displace;
		
		Polygon p = new Polygon();
		p.addPoint(Math.round((float) (x + x_len_displace)),
				Math.round((float) (y - y_len_displace))); // top of triangle
		p.addPoint(Math.round((float) (x_bottom + y_width_displace)),
				Math.round((float) (y_bottom + x_width_displace))); // right
																	// wing
		p.addPoint(Math.round((float) (x_bottom - y_width_displace)),
				Math.round((float) (y_bottom - x_width_displace))); // left wing

		g.setColor(tagged ? Color.magenta : Color.red);
		g.drawPolygon(p);
		g.fillPolygon(p);

		g.setColor(Color.green);
		g.drawString("" + organism.getFitness(), Math.round((float) x) + 8, Math.round((float) y) + 8);
	}

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
