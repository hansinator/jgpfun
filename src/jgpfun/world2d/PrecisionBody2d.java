package jgpfun.world2d;

/**
 *
 * @author hansinator
 */
public class PrecisionBody2d extends Body2d {

    //double precision coordinates
    public double dx, dy;

    //caching
    public double cosdir;

    public PrecisionBody2d(int x, int y, double dir, FoodFinder foodFinder) {
        super(x, y, dir, foodFinder);

        dx = x;
        dy = y;
        cosdir = Math.cos(dir);
    }
}
