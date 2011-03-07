package jgpfun.world2d;

import jgpfun.Settings;

public class TankMotor implements Motor2d {

    public static final double maxSteerForce = Settings.getDouble("maxSteerForce");

    public static final double maxSpeed = Settings.getDouble("maxSpeed");

    private final Body2d body;


    public TankMotor(Body2d body) {
        this.body = body;
    }


    //compute movement here
    @Override
    public void move(double left, double right) {
        double speed;

        //find the direction
        body.dir += (right - left) * (maxSteerForce / 100.0);
        //max speed is just a tweaking parameter; don't get confused by it
        //try varying it in simulation
        speed = (right + left) / 2.0;
        body.lastSpeed = speed;
        body.x += Math.sin(body.dir) * maxSpeed * speed / 10.0;
        body.y -= Math.cos(body.dir) * maxSpeed * speed / 10.0;
    }

}
