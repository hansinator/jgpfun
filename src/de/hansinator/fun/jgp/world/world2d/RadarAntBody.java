package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.actors.Motor2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
public class RadarAntBody extends Body2d {

	public final WallSense wallSense;

	public final Motor2d motor;

	private static final int foodPickupRadius = Settings
			.getInt("foodPickupRadius");

	private final Organism2d organism;

	private final World2d world;

	public final RadarSense radarSense;

	// cache motor outputs
	private int left, right;

	private static int NUM_INPUTS = 5;

	private static int NUM_OUTPUTS = 2;

	public RadarAntBody(Organism2d organism, World2d world) {
		super(0.0, 0.0, 0.0, new SensorInput[NUM_INPUTS],
				new ActorOutput[NUM_OUTPUTS]);
		this.organism = organism;
		this.world = world;

		// init senses
		wallSense = new WallSense(world.worldWidth, world.worldHeight, this);
		radarSense = new RadarSense(this, world);

		// inputs
		inputs[0] = new OrientationSense();
		inputs[1] = radarSense;
		inputs[2] = new RadarSenseDirection();
		inputs[3] = new SpeedSense();
		inputs[4] = wallSense;

		// outputs
		motor = new TankMotor(this);
		outputs[0] = new ActorOutput() {

			@Override
			public void set(int value) {
				left = value;
			}
		};
		outputs[1] = new ActorOutput() {

			@Override
			public void set(int value) {
				right = value;
			}
		};
	}

	@Override
	public void prepareInputs() {
	}

	@Override
	public void applyOutputs() {
		// limit, scale and set motor outputs
		motor.move(Math.max(0, Math.min(left, 65535))
				/ Organism2d.intScaleFactor,
				Math.max(0, Math.min(right, 65535)) / Organism2d.intScaleFactor);

		// pickup wallsense before coordinates are clipped
		wallSense.sense();
	}

	@Override
	public void postRoundTrigger() {
		Food food = world.findNearestFood(this);
		if ((food.x >= (x - foodPickupRadius))
				&& (food.x <= (x + foodPickupRadius))
				&& (food.y >= (y - foodPickupRadius))
				&& (food.y <= (y + foodPickupRadius))) {
			organism.incFood();
			food.randomPosition();
		}
	}

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
		final int x_center = Math.round((float) x);
		final int y_center = Math.round((float) y);

		Polygon p = new Polygon();
		p.addPoint(Math.round((float) (x + x_len_displace)),
				Math.round((float) (y - y_len_displace))); // top of triangle
		p.addPoint(Math.round((float) (x_bottom + y_width_displace)),
				Math.round((float) (y_bottom + x_width_displace))); // right
																	// wing
		p.addPoint(Math.round((float) (x_bottom - y_width_displace)),
				Math.round((float) (y_bottom - x_width_displace))); // left wing

		if (radarSense.target == null) {
			g.setColor(Color.darkGray);
			double rdir, bdir;
			rdir = radarSense.direction
					- ((double) Math
							.round(radarSense.direction / (2 * Math.PI)) * 2 * Math.PI);
			bdir = dir
					- ((double) Math.round(dir / (2 * Math.PI)) * 2 * Math.PI);
			g.drawLine(
					x_center,
					y_center,
					Math.round((float) (x + RadarSense.beamLength
							* Math.sin(rdir + bdir))),
					Math.round((float) (y - RadarSense.beamLength
							* Math.cos(rdir + bdir))));
		} else {
			if (radarSense.target != null) {
				g.setColor(Color.blue);
				g.drawLine(x_center, y_center,
						(int) Math.round(radarSense.target.getX()),
						(int) Math.round(radarSense.target.getY()));
			}
		}

		g.setColor(tagged ? Color.magenta : Color.red);
		g.drawPolygon(p);
		g.fillPolygon(p);

		g.setColor(Color.green);
		g.drawString("" + organism.getFitness(), x_center + 8, y_center + 8);
	}

	private class RadarSenseDirection implements SensorInput {

		@Override
		public int get() {
			return (int) (radarSense.direction * Organism2d.intScaleFactor);
		}

	}

	public static int getNumOutputs() {
		return NUM_OUTPUTS;
	}

	public static int getNumInputs() {
		return NUM_INPUTS;
	}
}
