package jgpfun.world2d;

import jgpfun.life.SensorInput;

public class Body2d {

    private final SensorInput[] inputs;

    public WallSense wallSense;

    public final Motor2d motor;

    public FoodFinder foodFinder;

    public Food food;

    public double foodDist;

    public double dir;

    public double x;

    public double y;

    public double lastSpeed = 0.0;


    public Body2d(double x, double y, double dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;

        this.motor = new TankMotor(this);

        inputs = new SensorInput[7];
        inputs[0] = new OrientationSense();
        inputs[1] = new FoodDirXSense();
        inputs[2] = new FoodDirYSense();
        inputs[3] = new FoodDistSense();
        //XXX
        inputs[4] = new FoodDistSense2();
        inputs[5] = new SpeedSense();
        inputs[6] = new WallSensorInput();
    }


    public SensorInput[] getInputs() {
        return inputs;
    }

    private class OrientationSense implements SensorInput {

        @Override
        public int get() {
            //could also be sin
            return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
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
