package jgpfun.world2d;

public class Body2d {

    public final TankMotor motor;

    public final FoodFinder foodFinder;


    public Body2d(TankMotor motor, FoodFinder foodFinder) {
        this.motor = motor;
        this.foodFinder = foodFinder;
    }

}
