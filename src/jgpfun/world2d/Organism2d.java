package jgpfun.world2d;

import jgpfun.jgp.OpCode;
import jgpfun.jgp.EvoVM;
import jgpfun.BaseOrganism;
import jgpfun.Genome;
import jgpfun.Settings;

/*
 * TODO: Create a loopback sense that represents the differential (ableitung)
 * of an output. Also create an integrator. This should ease temporal
 * memory functions.
 */
/**
 *
 * @author hansinator
 */
public class Organism2d extends BaseOrganism {

    static final double intScaleFactor = Settings.getDouble("intScaleFactor");

    static final int registerCount = Settings.getInt("registerCount");

    protected final Genome genome;

    public final EvoVM vm;

    public final Body2d[] bodies;

    private int food;


    public Organism2d(Genome genome, World2d world) {
        this.genome = genome;
        this.vm = new EvoVM(registerCount, genome.program.toArray(new OpCode[genome.program.size()]));
        this.food = 0;

        bodies = new Body2d[1];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body2d(rnd.nextInt(world.worldWidth), rnd.nextInt(world.worldHeight), rnd.nextDouble() * 2 * Math.PI, world.foodFinder, new WallSense(world.worldWidth, world.worldHeight));
        }
    }


    public void live() throws Exception {
        double left, right, foodDist;

        //write input registers
        int inreg = 0;
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(Math.round((float) b.x), Math.round((float) b.y));
            foodDist = b.foodFinder.foodDist(b.food, Math.round((float) b.x), Math.round((float) b.y));

            //cached cosdir and scale as int are meant to speed this up
            //vm.regs[inreg++] = (int) (((PrecisionBody2d) b).cosdir * scale);
            vm.regs[inreg++] = (int) (Math.cos(b.dir) * intScaleFactor);

            vm.regs[inreg++] = (int) (((b.food.x - b.x) / foodDist) * intScaleFactor);
            vm.regs[inreg++] = (int) (((b.food.y - b.y) / foodDist) * intScaleFactor);
            vm.regs[inreg++] = (int) (foodDist * intScaleFactor);
            vm.regs[inreg++] = Math.round((float) foodDist);
            vm.regs[inreg++] = (int) (b.lastSpeed * intScaleFactor);

            //wallsense
            vm.regs[inreg++] = b.wallSense.lastSenseVal;
        }

        vm.run();

        //use output values
        for (Body2d b : bodies) {
            //fetch, limit and scale outputs
            left = Math.max(0, Math.min(vm.regs[inreg++], 65535)) / intScaleFactor;
            right = Math.max(0, Math.min(vm.regs[inreg++], 65535)) / intScaleFactor;

            //move
            b.motor.move(left, right);

            //pickup wallsense before coordinates are clipped
            b.wallSense.sense();
        }
    }
    

    public Genome getGenome() {
        return genome;
    }


    @Override
    public int getFitness() {
        return food;
    }


    public void incFood() {
        food++;
    }

}
