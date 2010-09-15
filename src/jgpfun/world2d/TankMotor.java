package jgpfun.world2d;

import jgpfun.Food;
import jgpfun.Organism;

public class TankMotor {

    public double dir;

    public int x;

    public int y;

    public Food food;


    public TankMotor(int x, int y, double dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }


    //compute movement here
    public void move(double left, double right) {
        double speed;
        //find the direction
        dir += (right - left) * (Organism.maxForce / 100);
        //max speed is just a twaking parameter; don't get confused by it
        //try varying it in simulation
        speed = (right + left) / 2;
        x += Math.sin(dir) * Organism.maxSpeed * speed / 10;
        y -= Math.cos(dir) * Organism.maxSpeed * speed / 10;
    }
}
