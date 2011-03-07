/*
 */
package jgpfun.testing;

import java.security.SecureRandom;
import java.util.Random;
import jgpfun.world2d.Body2d;
import jgpfun.world2d.TankMotor;
import jgpfun.world2d.WallSense;

/**
 *
 * @author Hansinator
 */
public class DebugTankMotorMotion {

    private Body2d body;

    private static final Random rnd = new SecureRandom();


    public void move(double left, double right) {
        double speed;

        //find the direction
        double dirAmp = (TankMotor.maxSteerForce / 100.0);
        double dirDelta = (right - left) * dirAmp;
        body.dir += dirDelta;



        //max speed is just a twaking parameter; don't get confused by it
        //try varying it in simulation
        speed = (right + left) / 2.0;

        double xDelta = Math.sin(body.dir) * TankMotor.maxSpeed * speed / 10.0;
        double yDelta = Math.cos(body.dir) * TankMotor.maxSpeed * speed / 10.0;

        System.out.println("dirDelta = " + dirDelta);
        System.out.println("dir      = " + body.dir);
        System.out.println("speed    = " + speed);
        System.out.println("sinDir   = " + Math.sin(body.dir));
        System.out.println("cosDir   = " + Math.cos(body.dir));
        System.out.println("xDelta   = " + xDelta);
        System.out.println("yDelta   = " + yDelta);

        body.x += xDelta;
        body.y -= yDelta;

        System.out.println("body.x = " + body.x);
        System.out.println("body.y = " + body.y);

        System.out.println("");
    }


    public void test(int iterations) {
        for (int i = 0; i < iterations; i++) {
            //move(7.9998779296875, 5.65673828125);
            //move(5.65673828125, 7.9998779296875);
            move(0, 1.0);
        }
    }


    public DebugTankMotorMotion(int width, int height) {
        body = new Body2d(5.0, 5.0, 0.0, null, new WallSense(width, height));
    }


    public static void main(String[] args) {
        new DebugTankMotorMotion(2048, 1536).test(63);
    }

}
