package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;
import de.hansinator.fun.jgp.util.Settings;

/**
 *
 * @author Hansinator
 */
public class FoodAntBody extends Body2d {

    public final WallSense wallSense;

    public final Motor2d motor;

    private static final int foodPickupRadius = Settings.getInt("foodPickupRadius");

    private final Organism2d organism;

    private final World2d world;

    private Food food;

    private double foodDist;


    public FoodAntBody(Organism2d organism, World2d world) {
        super(0.0, 0.0, 0.0, new SensorInput[7]);
        this.organism = organism;
        this.world = world;

        //init senses
        wallSense = new WallSense(world.worldWidth, world.worldHeight, this);

        //outputs
        this.motor = new TankMotor(this);

        //inputs
        inputs[0] = new OrientationSense();
        inputs[1] = new FoodDirXSense();
        inputs[2] = new FoodDirYSense();
        inputs[3] = new FoodDistSense();
        inputs[4] = new FoodDistSense2(); //fix?
        inputs[5] = new SpeedSense();
        inputs[6] = wallSense;
    }


    @Override
    public void prepareInputs() {
        food = world.findNearestFood(this);
        foodDist = World2dObject.distance(food, Math.round((float) x), Math.round((float) y));
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
        final double sindir = Math.sin(dir);
        final double cosdir = Math.cos(dir);
        final double x_len_displace = 6.0 * sindir;
        final double y_len_displace = 6.0 * cosdir;
        final double x_width_displace = 4.0 * sindir;
        final double y_width_displace = 4.0 * cosdir;
        final double x_bottom = x - x_len_displace;
        final double y_bottom = y + y_len_displace;
        final int x_center = Math.round((float)x);
        final int y_center = Math.round((float)y);

        Polygon p = new Polygon();
        p.addPoint(Math.round((float)(x + x_len_displace)), Math.round((float)(y - y_len_displace))); //top of triangle
        p.addPoint(Math.round((float)(x_bottom + y_width_displace)), Math.round((float)(y_bottom + x_width_displace))); //right wing
        p.addPoint(Math.round((float)(x_bottom - y_width_displace)), Math.round((float)(y_bottom - x_width_displace))); //left wing

        g.setColor(Color.darkGray);
        g.drawLine(x_center, y_center, (int)Math.round(food.x), (int)Math.round(food.y));

        g.setColor(Color.red);
        g.drawPolygon(p);
        g.fillPolygon(p);

        g.setColor(Color.green);
        g.drawString("" + organism.getFitness(), x_center + 8, y_center + 8);
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
}
