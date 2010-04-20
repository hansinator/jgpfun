/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun;

import java.security.SecureRandom;
import java.util.Random;

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
    public int x, y, food;
    public double dir;

    public static final double maxForce = 10;
    public static final double maxSpeed = 6;    

    public Organism(OpCode[] program, int x, int y, double dir) {
        this.x = x;
        this.y = y;
        this.program = program;
        this.dir = dir;
        this.food = 0;
        this.vm = new EvoVM2(24, program);
        cosdir = Math.cos(dir);
    }

    //public long vmrun, allrun, comp;

    double cosdir;
    void live(Food nextFood, double foodDist) throws Exception {
        int left, right, scale = 65535;//(int)((Integer.MAX_VALUE / (2.0*Math.PI)));
        double speed;

        //long start2 = System.nanoTime();

        //write input registers
        /*System.out.println("");
        System.out.println("Scale " + scale);
        System.out.println("Food dist " + foodDist);
        System.out.println("fooddistx " + (nextFood.x - x));
        System.out.println("fooddisty " + (nextFood.y - y));
        System.out.println("foodx " + (((nextFood.x - x) / foodDist) * scale));
        System.out.println("foody " + (((nextFood.y - y) / foodDist) * scale));*/
        vm.regs[0] = (int)(cosdir * scale);
        vm.regs[1] = (int)(((nextFood.x - x) / foodDist) * scale);
        vm.regs[2] = (int)(((nextFood.y - y) / foodDist) * scale);

        //long start = System.nanoTime();
        
        vm.run();

        //vmrun = (System.nanoTime() - start);
        //start = System.nanoTime();

        //fetch and limit outputs
        left =  vm.regs[3] / scale;
        right = vm.regs[4] / scale;
        /*left =  Math.max(0, Math.min(vm.regs[3], 65535)) / scale;
        right = Math.max(0, Math.min(vm.regs[4], 65535)) / scale;*/

        //compute movement here
        //dir += (right - left) * (maxForce / 50000);   //find the direction
        dir += ((right - left) / 60000.0);   //find the direction
        speed = (right + left) / 2;
        cosdir = Math.cos(dir);

        /*System.out.println("dirdelta: " + ((right - left) / 10000.0));
        System.out.println("xdelta: " + ((Math.sin(dir) *  speed) / 2000));
        System.out.println("ydelta: " + ((cosdir * speed) / 2000));*/

        /*x += Math.sin(dir) * maxSpeed * speed / 20000;       //max speed is just a twaking parameter; don't get confused by it
        y -= cosdir * maxSpeed * speed / 20000;       //try varying it in simulation*/

        x += ((Math.sin(dir) *  speed) / 50000.0);
        y -= ((cosdir * speed) / 50000.0);

        //comp = (System.nanoTime() - start);
        //allrun = (System.nanoTime() - start2);
    }

    static Organism randomOrganism(int xmax, int ymax, int progsize) {
        OpCode[] program = new OpCode[rnd.nextInt(progsize)];

        for (int i = 0; i < program.length; i++) {
            program[i] = OpCode.randomOne(rnd);
        }

        return new Organism(program, rnd.nextInt(xmax), rnd.nextInt(ymax), rnd.nextDouble());
    }

    @Override
    public OpCode[] clone() {
        OpCode[] p = new OpCode[program.length];

        for(int i = 0; i < program.length; i++) {
            p[i] = program[i].clone();
        }

        return p;
    }

    public int compareTo(Organism o) {
        return new Integer(food).compareTo(o.food);
    }
}
