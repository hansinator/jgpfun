package jgpfun.world2d;

import java.awt.Graphics;
import jgpfun.life.SensorInput;

public abstract class Body2d {

    public double dir;

    public double x;

    public double y;

    protected final SensorInput[] inputs;

    protected double lastSpeed = 0.0;


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

    public abstract void draw(Graphics g);

    protected class OrientationSense implements SensorInput {

        @Override
        public int get() {
            //could also be sin
            return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
        }

    }
}
