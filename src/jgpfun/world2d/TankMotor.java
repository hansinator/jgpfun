package jgpfun.world2d;

import jgpfun.Organism;

public class TankMotor implements Motor2d {

    private final Body2d body;


    public TankMotor(Body2d body) {
        this.body = body;
    }


    //compute movement here
    @Override
    public void move(double left, double right) {
        double speed;

        //find the direction
        body.dir += (right - left) * (Organism.maxSteerForce / 100.0);
        //max speed is just a tweaking parameter; don't get confused by it
        //try varying it in simulation
        speed = (right + left) / 2.0;
        body.x += Math.sin(body.dir) * Organism.maxSpeed * speed / 10.0;
        body.y -= Math.cos(body.dir) * Organism.maxSpeed * speed / 10.0;
    }

}
