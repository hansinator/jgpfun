/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun;

import jgpfun.world2d.FoodFinder;
import jgpfun.world2d.TankMotor;
import jgpfun.jgp.OpCode;
import jgpfun.jgp.EvoVM2;
import java.security.SecureRandom;
import java.util.Random;
import jgpfun.world2d.Body2d;

/**
 *
 * @author hansinator
 */
public class Organism implements Comparable<Organism> {

    static Random rnd = new SecureRandom();

    EvoVM2 vm;

    OpCode[] program;
    /*int xpos, ypos;
    int angle;*/

    public int x, y;

    double dx, dy;

    public double dir;

    //public long vmrun, allrun, comp;
    double cosdir;

    public int food;

    public Body2d[] bodies;

    public boolean showdebug = false;

    public static final double maxForce = 10.0;

    public static final double maxSpeed = 4.0;

    //PC - keep this for movement reasons
    /*
    public Organism(OpCode[] program, int x, int y, double dir) {
    this.x = x;
    this.y = y;
    dx = x;
    dy = y;
    this.dir = dir;
    this.food = 0;
    cosdir = Math.cos(dir);

    this.program = program;
    this.vm = new EvoVM2(24, program);
    }*/

    //LAPPY
    public Organism(OpCode[] program, int worldWidth, int worldHeight, FoodFinder foodFinder) {
        this.program = program;
        this.vm = new EvoVM2(24, program);
        this.food = 0;

        bodies = new Body2d[2];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body2d(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), rnd.nextDouble(), foodFinder);
        }
    }

    //LAPPY

    void live() throws Exception {
        int out1, out2;
        double left, right, foodDist;
        double scale = 8192.0;

        //write input registers
        int inreg = 0;
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(b.x, b.y);
            foodDist = b.foodFinder.foodDist(b.food, b.x, b.y);

            vm.regs[inreg++] = (int) (Math.cos(b.dir) * scale);
            vm.regs[inreg++] = (int) (((b.food.x - b.x) / foodDist) * scale);
            vm.regs[inreg++] = (int) (((b.food.y - b.y) / foodDist) * scale);
        }

        vm.run();

        //fetch outputs
        for (Body2d b : bodies) {
            out1 = vm.regs[inreg++];
            out2 = vm.regs[inreg++];

            //scale
            left = Math.max(0, Math.min(out1, 65535)) / scale;
            right = Math.max(0, Math.min(out2, 65535)) / scale;

            //move
            b.motor.move(left, right);
        }
    }


    //PC
    void live(Food nextFood, double foodDist) throws Exception {
        int left, right, scale = 65535;//(int)((Integer.MAX_VALUE / (2.0*Math.PI)));
        double speed;

        //long start2 = System.nanoTime();

        //write input registers
        if (showdebug) {
            System.out.println("");
            System.out.println("Food dist " + foodDist);
            System.out.println("fooddistx " + (nextFood.x - x));
            System.out.println("fooddisty " + (nextFood.y - y));
            System.out.println("foodx "
                    + (((nextFood.x - x) / foodDist) * scale));
            System.out.println("foody "
                    + (((nextFood.y - y) / foodDist) * scale));
        }
        vm.regs[0] = (int) (cosdir * scale);
        vm.regs[1] = (int) (cosdir * scale);
        vm.regs[2] = (int) (((nextFood.x - x) / foodDist) * scale);
        vm.regs[3] = (int) (((nextFood.y - y) / foodDist) * scale);
        //GPS!
        vm.regs[4] = x;
        vm.regs[5] = y;

        //long start = System.nanoTime();

        vm.run();

        //vmrun = (System.nanoTime() - start);
        //start = System.nanoTime();

        //fetch and limit outputs
        left = vm.regs[6] / scale;
        right = vm.regs[7] / scale;
        /*left =  Math.max(0, Math.min(vm.regs[3], 65535)) / scale;
        right = Math.max(0, Math.min(vm.regs[4], 65535)) / scale;*/

        //compute movement here
        //dir += (right - left) * (maxForce / 50000);   //find the direction
        dir += ((right - left) / 160000.0);   //find the direction
        speed = (right + left) / 2;
        cosdir = Math.cos(dir);

        if (showdebug) {
            System.out.println("");
            System.out.println("dirdelta: " + ((right - left) / 160000.0));
            System.out.println("xdelta: " + ((Math.sin(dir) * speed) / 30000.0));
            System.out.println("ydelta: " + ((cosdir * speed) / 30000.0));
            System.out.println("cur x: " + x);
            System.out.println("cur y: " + y);
        }

        /*x += Math.sin(dir) * maxSpeed * speed / 20000;       //max speed is just a twaking parameter; don't get confused by it
        y -= cosdir * maxSpeed * speed / 20000;       //try varying it in simulation*/

        dx += ((Math.sin(dir) * speed) / 30000.0);
        dy -= ((cosdir * speed) / 30000.0);
        x = (int) dx;
        y = (int) dy;

        if (showdebug) {
            System.out.println("cur2 x: " + x);
            System.out.println("cur2 y: " + y);
        }

        //comp = (System.nanoTime() - start);
        //allrun = (System.nanoTime() - start2);
    }


    static Organism randomOrganism(int xmax, int ymax, int progsize, FoodFinder foodFinder) {
        OpCode[] program = new OpCode[rnd.nextInt(progsize)];

        for (int i = 0; i < program.length; i++) {
            program[i] = OpCode.randomOpCode(rnd);
        }

        return new Organism(program, xmax, ymax, foodFinder);
    }


    @Override
    public OpCode[] clone() {
        OpCode[] p = new OpCode[program.length];

        for (int i = 0; i < program.length; i++) {
            p[i] = program[i].clone();
        }

        return p;
    }


    @Override
    public int compareTo(Organism o) {
        return new Integer(food).compareTo(o.food);
    }
}
