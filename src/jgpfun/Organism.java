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

    public int food;

    public TankMotor[] motors;

    public static final double maxForce = 10.0;

    public static final double maxSpeed = 4.0;


    public Organism(OpCode[] program, int worldWidth, int worldHeight) {
        this.program = program;
        this.vm = new EvoVM(24, program);
        this.food = 0;

        motors = new TankMotor[2];
        for(int i = 0; i < 2; i++) {
            motors[i] = new TankMotor(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), rnd.nextDouble());
        }
    }


    void live(PopulationManager.FoodFinder fd) throws Exception {
        int out1, out2;
        double left, right, foodDist;
        double scale = 8192.0;

        //write input registers
        int inreg = 0;
        for (TankMotor m : motors) {
            m.food = fd.findNearestFood(m.x, m.y);
            foodDist = fd.foodDist(m.food, m.x, m.y);
            
            vm.regs[inreg++] = (int) (Math.cos(m.dir) * scale);
            vm.regs[inreg++] = (int) (((m.food.x - m.x) / foodDist) * scale);
            vm.regs[inreg++] = (int) (((m.food.y - m.y) / foodDist) * scale);
        }

        vm.run();

        //fetch outputs
        for (TankMotor m : motors) {
            out1 = vm.regs[inreg++];
            out2 = vm.regs[inreg++];

            //scale
            left = Math.max(0, Math.min(out1, 65535)) / scale;
            right = Math.max(0, Math.min(out2, 65535)) / scale;

            //move
            m.move(left, right);
        }
    }


    static Organism randomOrganism(int xmax, int ymax, int progsize) {
        OpCode[] program = new OpCode[rnd.nextInt(progsize)];

        for (int i = 0; i < program.length; i++) {
            program[i] = OpCode.randomOne(rnd);
        }

        return new Organism(program, xmax, ymax);
    }


    @Override
    public OpCode[] clone() {
        OpCode[] p = new OpCode[program.length];

        for (int i = 0; i < program.length; i++) {
            p[i] = program[i].clone();
        }

        return p;
    }

    public class TankMotor {

        public double dir;

        public int x, y;

        public Food food;


        public TankMotor(int x, int y, double dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        //compute movement here

        private void move(double left, double right) {
            double speed;

            //find the direction
            dir += (right - left) * (maxForce / 100);

            //max speed is just a twaking parameter; don't get confused by it
            //try varying it in simulation
            speed = (right + left) / 2;
            x += Math.sin(dir) * maxSpeed * speed / 10;
            y -= Math.cos(dir) * maxSpeed * speed / 10;
        }

    }
}
