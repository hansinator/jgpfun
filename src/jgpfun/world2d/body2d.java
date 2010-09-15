package jgpfun.world2d;

import jgpfun.Food;

public class Body2d {

    public final Motor2d motor;

    public final FoodFinder foodFinder;

    public Food food;

    public double dir;

    public int x;

    public int y;


    public Body2d(int x, int y, double dir, FoodFinder foodFinder) {
        this.x = x;
        this.y = y;
        this.dir = dir;

        this.motor = new TankMotor(this);
        this.foodFinder = foodFinder;
    }

}
