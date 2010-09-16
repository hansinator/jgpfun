package jgpfun.world2d;

/**
 *
 * @author hansinator
 */
public class PrecisionTankMotor2d implements Motor2d {

    private final PrecisionBody2d body;


    public PrecisionTankMotor2d(PrecisionBody2d body) {
        this.body = body;
    }


    //compute movement here
    @Override
    public void move(double left, double right) {
        double speed;

        //find the direction
        body.dir += ((right - left) / 160000.0);
        body.cosdir = Math.cos(body.dir);

        //compute speed
        speed = (right + left) / 2;

        //compute position delta and scale down by big factor
        body.dx += ((Math.sin(body.dir) * speed) / 30000.0);
        body.dy -= ((body.cosdir * speed) / 30000.0);

        //set world coordinates
        body.x = (int) body.dx;
        body.y = (int) body.dy;
    }

}
