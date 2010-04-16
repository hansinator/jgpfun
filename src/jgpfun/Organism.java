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
public class Organism {

    static Random rnd = new SecureRandom();
    EvoVM vm;
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
        this.vm = new EvoVM(24, program);
    }

    void live(Food nextFood, double foodDist) throws Exception {
        int out1, out2;
        double left, right, speed;
        double scale = 8192.0;

        //write input registers
        vm.regs[0] = (int)(Math.cos(dir) * scale);
        vm.regs[1] = (int)(((nextFood.x - x) / foodDist) * scale);
        vm.regs[2] = (int)(((nextFood.y - y) / foodDist) * scale);

        vm.run();

        //fetch outputs
        out1 = vm.regs[3];
        out2 = vm.regs[4];

        left = Math.max(0, Math.min(out1, 65535)) / scale;
        right = Math.max(0, Math.min(out2, 65535)) / scale;

        //compute movement here
        dir += (right - left) * (maxForce / 100);   //find the direction
        speed = (right + left) / 2;
        x += Math.sin(dir) * maxSpeed * speed / 10;       //max speed is just a twaking parameter; don't get confused by it
        y -= Math.cos(dir) * maxSpeed * speed / 10;       //try varying it in simulation        
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
}
