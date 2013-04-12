package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;

import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
public class FoodAntBody extends Body2d {

	public final WallSense wallSense;

	public final TankMotor motor;
	
	public final ObjectLocator locator;

	private static final int foodPickupRadius = Settings
			.getInt("foodPickupRadius");

	private final World2d world;

	private Food food;

	private static int NUM_INPUTS = 7;

	private static int NUM_OUTPUTS = 2;

	public FoodAntBody(Organism2d organism, World2d world) {
		super(organism, 0.0, 0.0, 0.0, new SensorInput[NUM_INPUTS], new ActorOutput[NUM_OUTPUTS]);
		this.world = world;

		// init senses
		wallSense = new WallSense(world.worldWidth, world.worldHeight, this);
		locator = new ObjectLocator(this, world);

		// inputs
		inputs[0] = new OrientationSense();
		inputs[1] = locator.senseDirX;
		inputs[2] = locator.senseDirY;
		inputs[3] = locator.senseDist;
		inputs[4] = locator.senseDist2; // fix?
		inputs[5] = new SpeedSense();
		inputs[6] = wallSense;
		
        //outputs
        motor = new TankMotor(this);
        outputs[0] = motor.actorLeft;
		outputs[1] = motor.actorRight;
	}

	@Override
	public void prepareInputs() {
		locator.locate();
		food = locator.target;
	}

	@Override
	public void applyOutputs() {
		// move body around
		motor.move();

		// pickup wallsense before coordinates are clipped
		wallSense.sense();
	}

	@Override
	public void postRoundTrigger() {
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
		g.setColor(Color.darkGray);
		g.drawLine(Math.round((float) x), Math.round((float) y),
				(int) Math.round(food.x), (int) Math.round(food.y));
		super.draw(g);
	}

	public static int getNumOutputs() {
		return NUM_OUTPUTS;
	}

	public static int getNumInputs() {
		return NUM_INPUTS;
	}
}
