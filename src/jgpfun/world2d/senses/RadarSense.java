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

    public static final double beamLength = 200.0;

    public Point target = null;


    public RadarSense(Body2d body, World2d world) {
        this.body = body;
        this.world = world;
    }


    public boolean pointInLine(double x1, double y1, double x2, double y2, Point p) {
        double x3, y3;

        //point on line
        x3 = Math.floor(p.x);
        y3 = Math.floor(p.y);

        //return ((x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1)) > 0;

        double m, b;
        m = (y2 - y1) / (x2 - x1);
        b = y1 - m * x1;

        double y = m * x3 + b;
        if (Math.round(y) == Math.round(y3)) {
            return true;
        }
        return false;
    }


    @Override
    public int get() {
        double x1, y1, x2, y2, rdir, bdir;

        //line start
        x1 = Math.floor(body.x);
        y1 = Math.floor(body.y);

        //line end
        rdir = direction - ((double) Math.round(direction / (2 * Math.PI)) * 2 * Math.PI);
        bdir = body.dir - ((double) Math.round(body.dir / (2 * Math.PI)) * 2 * Math.PI);
        x2 = Math.floor(body.x + beamLength * Math.sin(rdir + bdir));
        y2 = Math.floor(body.y - beamLength * Math.cos(rdir + bdir));

        for (Food f : world.food) {
            if (pointInLine(x1, y1, x2, y2, f) && (Math.sqrt(((x1 - f.x) * (x1 - f.x)) + ((y1 - f.y) * (y1 - f.y))) <= beamLength) && (Math.abs(x1 + 2.0 * Math.sin(rdir+bdir) - f.x) < Math.abs(x1 - f.x))) {
                target = f;
                return Integer.MAX_VALUE;
            }
        }
        target = null;
        return 0;
    }

}
