package jgpfun.world2d;

import jgpfun.life.SensorInput;
import jgpfun.util.Settings;
import jgpfun.world2d.senses.WallSense;

/**
 *
 * @author Hansinator
 */
public class FoodAntBody extends Body2d {

    public static final int foodPickupRadius = Settings.getInt("foodPickupRadius");

    public WallSense wallSense;

    public final Motor2d motor;

    public FoodFinder foodFinder;

    public Food food;

    public double foodDist;

    private final Organism2d organism;


    public FoodAntBody(Organism2d organism) {
        super(0.0, 0.0, 0.0, new SensorInput[7]);
        this.organism = organism;

        //outputs
        this.motor = new TankMotor(this);

        //inputs
        inputs[0] = new OrientationSense();
        inputs[1] = new FoodDirXSense();
        inputs[2] = new FoodDirYSense();
        inputs[3] = new FoodDistSense();
        inputs[4] = new FoodDistSense2(); //fix?
        inputs[5] = new SpeedSense();
        inputs[6] = new WallSensorInput();
    }


    @Override
    public void prepareInputs() {
        food = foodFinder.findNearestFood(Math.round((float) x), Math.round((float) y));
        foodDist = foodFinder.foodDist(food, Math.round((float) x), Math.round((float) y));
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

    private class FoodDirXSense implements SensorInput {

        @Override
        public int get() {
            return (int) (((food.x - x) / foodDist) * Organism2d.intScaleFactor);
        }

    }

    private class FoodDirYSense implements SensorInput {

        @Override
        public int get() {
            return (int) (((food.y - y) / foodDist) * Organism2d.intScaleFactor);
        }

    }

    private class FoodDistSense implements SensorInput {

        @Override
        public int get() {
            return (int) (foodDist * Organism2d.intScaleFactor);
        }

    }

    private class FoodDistSense2 implements SensorInput {

        @Override
        public int get() {
            return Math.round((float) foodDist);
        }

    }

    private class SpeedSense implements SensorInput {

        @Override
        public int get() {
            return (int) (lastSpeed * Organism2d.intScaleFactor);
        }

    }

    private class WallSensorInput implements SensorInput {

        @Override
        public int get() {
            //wallsense
            return wallSense.lastSenseVal;
        }

    }
}
