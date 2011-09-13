package jgpfun.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import jgpfun.life.SensorInput;
import jgpfun.util.Settings;
import jgpfun.world2d.senses.WallSense;

/**
 *
 * @author Hansinator
 */
public class FoodAntBody extends Body2d {

    public WallSense wallSense;

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
        food = world.findNearestFood(Math.round((float) x), Math.round((float) y));
        foodDist = World2d.foodDist(food, Math.round((float) x), Math.round((float) y));
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
        double sindir = Math.sin(dir);
        double cosdir = Math.cos(dir);
        int x_center = Math.round((float) x);
        int y_center = Math.round((float) y);
        int x_len_displace = (int) Math.round(6.0 * sindir);
        int y_len_displace = (int) Math.round(6.0 * cosdir);
        int x_width_displace = (int) Math.round(4.0 * sindir);
        int y_width_displace = (int) Math.round(4.0 * cosdir);
        int x_bottom = x_center - x_len_displace;
        int y_bottom = y_center + y_len_displace;

        Polygon p = new Polygon();
        p.addPoint(x_center + x_len_displace, y_center - y_len_displace); //top of triangle
        p.addPoint(x_bottom + y_width_displace, y_bottom + x_width_displace); //right wing
        p.addPoint(x_bottom - y_width_displace, y_bottom - x_width_displace); //left wing

        g.setColor(Color.darkGray);
        g.drawLine(x_center, y_center, food.x, food.y);

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
