package de.hansinator.fun.jgp.world2d;

import java.awt.Graphics;
import de.hansinator.fun.jgp.world2d.senses.SensorInput;

public abstract class Body2d extends World2dObject {

    protected final SensorInput[] inputs;

    protected double lastSpeed = 0.0;

    public double dir;

    public volatile boolean tagged = false;


    public Body2d(double x, double y, double dir, SensorInput[] inputs) {
        //TODO: fix null pointer
        super(null, x, y);
        this.dir = dir;
        this.inputs = inputs;
    }


    public SensorInput[] getInputs() {
        return inputs;
    }


    public abstract void prepareInputs();


    public abstract void postRoundTrigger();


    @Override
    public abstract void draw(Graphics g);

    protected class OrientationSense implements SensorInput {

        @Override
        public int get() {
            //could also be sin
            return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
        }

    }
}
