package jgpfun.world2d;

import jgpfun.Food;

public class Body2d {

    public final TankMotor motor;

    public final FoodFinder foodFinder;

    public Food food;


    public Body2d(TankMotor motor, FoodFinder foodFinder) {
        this.motor = motor;
        this.foodFinder = foodFinder;
    }

}
