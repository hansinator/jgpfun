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

	public final TankMotor motor;

	private static final int foodPickupRadius = Settings
			.getInt("foodPickupRadius");

	private final World2d world;

	public final RadarSense radarSense;

	private static int NUM_INPUTS = 5;

	private static int NUM_OUTPUTS = 3;
	

	public RadarAntBody(Organism2d organism, World2d world) {
		super(organism, 0.0, 0.0, 0.0, new SensorInput[NUM_INPUTS],
				new ActorOutput[NUM_OUTPUTS]);
		this.world = world;

		// init senses
		wallSense = new WallSense(world.worldWidth, world.worldHeight, this);
		radarSense = new RadarSense(this, world);

		// inputs
		inputs[0] = new OrientationSense();
		inputs[1] = radarSense;
		inputs[2] = radarSense.senseDirection;
		inputs[3] = new SpeedSense();
		inputs[4] = wallSense;

		// outputs
		motor = new TankMotor(this);
        outputs[0] = motor.actorLeft;
		outputs[1] = motor.actorRight;
		outputs[2] = radarSense;
	}

	@Override
	public void prepareInputs() {
	}

	@Override
	public void applyOutputs() {
		// limit, scale and set motor outputs
		motor.move();
		
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
		final int x_center = Math.round((float) x);
		final int y_center = Math.round((float) y);

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
		
		super.draw(g);
	}

	public static int getNumOutputs() {
		return NUM_OUTPUTS;
	}

	public static int getNumInputs() {
		return NUM_INPUTS;
	}
}
