/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun;

import jgpfun.jgp.OpCode;
import jgpfun.jgp.EvoVM;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.world2d.Body2d;
import jgpfun.world2d.WallSense;
import jgpfun.world2d.World2d;

/*
 * TODO: Create a loopback sense that represents the differential (ableitung)
 * of an output. Also create an integrator. This should ease temporal
 * memory functions.
 */
/**
 *
 * @author hansinator
 */
public class Organism implements Comparable<Organism> {

    //public static final double maxForce = 10.0;

    // testing lower max force to discourage immediate direction setting
    public static final double maxForce = 0.1;

    public static final double maxSpeed = 4.0;

    protected static final Random rnd = new SecureRandom();

    protected final int registerCount = 24;
    //protected final int registerCount = 32;

    protected final EvoVM vm;

    protected final List<OpCode> program;

    public final Body2d[] bodies;

    private int food;


    public Organism(List<OpCode> program, World2d world) {
        this.program = program;
        this.vm = new EvoVM(registerCount, program.toArray(new OpCode[program.size()]));
        this.food = 0;

        bodies = new Body2d[1];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body2d(rnd.nextInt(world.worldWidth), rnd.nextInt(world.worldHeight), rnd.nextDouble() * 2 * Math.PI, world.foodFinder, new WallSense(world.worldWidth, world.worldHeight));
        }
    }


    public void live() throws Exception {
        double left, right, foodDist;
        double scale = 8192.0;

        //write input registers
        int inreg = 0;
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(Math.round((float)b.x), Math.round((float)b.y));
            foodDist = b.foodFinder.foodDist(b.food, Math.round((float)b.x), Math.round((float)b.y));

            //cached cosdir and scale as int are meant to speed this up
            //vm.regs[inreg++] = (int) (((PrecisionBody2d) b).cosdir * scale);
            vm.regs[inreg++] = (int) (Math.cos(b.dir) * scale);

            vm.regs[inreg++] = (int) (((b.food.x - b.x) / foodDist) * scale);
            vm.regs[inreg++] = (int) (((b.food.y - b.y) / foodDist) * scale);
            vm.regs[inreg++] = (int) (foodDist * scale);
            vm.regs[inreg++] = Math.round((float)foodDist);

            //wallsense
            //vm.regs[inreg++] = b.wallSense.lastSenseVal;
        }

        vm.run();

        //use output values
        for (Body2d b : bodies) {
            //fetch, limit and scale outputs
            left = Math.max(0, Math.min(vm.regs[inreg++], 65535)) / scale;
            right = Math.max(0, Math.min(vm.regs[inreg++], 65535)) / scale;

            //move
            b.motor.move(left, right);

            //pickup wallsense before coordinates are clipped
            //b.wallSense.sense();
        }
    }


    static Organism randomOrganism(World2d world, int progSize) {
        int size = rnd.nextInt(progSize) + 1;
        List<OpCode> program = new ArrayList<OpCode>(size);

        for (int i = 0; i < size; i++) {
            program.add(OpCode.randomOpCode(rnd));
        }

        return new Organism(program, world);
    }


    @Override
    public List<OpCode> clone() {
        List<OpCode> p = new ArrayList<OpCode>(program.size());

        for (OpCode oc : program) {
            p.add(oc.clone());
        }

        return p;
    }


    @Override
    public int compareTo(Organism o) {
        return new Integer(food).compareTo(o.food);
    }


    public List<OpCode> getProgram() {
        return program;
    }


    public EvoVM getVm() {
        return vm;
    }


    public void incFood() {
        food++;
    }


    public int getFitness() {
        return food;
    }

}
