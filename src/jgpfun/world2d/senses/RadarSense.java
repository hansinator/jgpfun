package jgpfun.world2d.senses;

import java.awt.Point;
import jgpfun.life.SensorInput;
import jgpfun.world2d.Body2d;
import jgpfun.world2d.Food;
import jgpfun.world2d.World2d;

/**
 *
 * @author Hansinator
 */
public class RadarSense implements SensorInput {

    private final Body2d body;

    private final World2d world;

    public double direction = 0.0;

    public static final double beamLength = 50.0;


    public RadarSense(Body2d body, World2d world) {
        this.body = body;
        this.world = world;
    }


    public boolean pointInLine(Point p) {
        double x1, x2, x3, y1, y2, y3;

        //line start
        x1 = body.x;
        y1 = body.y;

        //line end
        x2 = body.x + beamLength * Math.sin(direction);
        x3 = body.y - beamLength * Math.cos(direction);

        //point on line
        y2 = Math.floor(p.y);
        y3 = Math.floor(p.x);

        double m, b;
        m = (y2 - y1) / (x2 - x1);
        b = y1 - m * x1;

        double y = m * x3 + b;
        if (y == y3) {
            return true;
        }
        return false;
    }


    @Override
    public int get() {
        for (Food f : world.food) {
            if (pointInLine(f)) {
                return Integer.MAX_VALUE;
            }
        }
        return 0;
    }

}
