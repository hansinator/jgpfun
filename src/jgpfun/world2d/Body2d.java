package jgpfun.world2d;

public class Body2d {

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
    }

}
