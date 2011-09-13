package jgpfun.world2d;

import jgpfun.life.SensorInput;

public abstract class Body2d {

    protected final SensorInput[] inputs;

    public double dir;

    public double x;

    public double y;

    public double lastSpeed = 0.0;


    public Body2d(double x, double y, double dir, SensorInput[] inputs) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.inputs = inputs;
    }


    public SensorInput[] getInputs() {
        return inputs;
    }


    public abstract void prepareInputs();

    public abstract void postRoundTrigger();

    protected class OrientationSense implements SensorInput {

        @Override
        public int get() {
            //could also be sin
            return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
        }

    }
}
