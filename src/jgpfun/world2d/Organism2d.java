package jgpfun.world2d;

import java.io.IOException;
import jgpfun.genetics.lgp.OpCode;
import jgpfun.life.BaseOrganism;
import jgpfun.genetics.Genome;
import jgpfun.genetics.lgp.BaseMachine;
import jgpfun.genetics.lgp.EvoVM;
import jgpfun.life.SensorInput;
import jgpfun.util.Settings;

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

    public final BaseMachine vm;

    public final Body2d[] bodies;

    private final SensorInput[] inputs;

    private int food;


    public Organism2d(Genome genome) throws IOException {
        super(genome);
        this.vm = new EvoVM(registerCount, genome.program.toArray(new OpCode[genome.program.size()]));
        //this.vm = EvoCompiler.compile(registerCount, genome.program.toArray(new OpCode[genome.program.size()]));
        this.food = 0;

        //init bodies and grab inputs
        bodies = new Body2d[1];
        inputs = new SensorInput[7 * bodies.length];
        int x = 0;
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body2d(0.0, 0.0, rnd.nextDouble() * 2 * Math.PI);
            for(SensorInput input : bodies[i].getInputs())
                inputs[x++] = input;
        }
    }


    public void addToWorld(World2d world) {
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].x = rnd.nextInt(world.worldWidth);
            bodies[i].y = rnd.nextInt(world.worldHeight);
            bodies[i].foodFinder = world.foodFinder;
            bodies[i].wallSense = new WallSense(world.worldWidth, world.worldHeight);
            bodies[i].wallSense.setBody(bodies[i]);
        }
    }


    @Override
    public void live() {
        double left, right;
        int reg = 0;

        //calculate food stuff for body (prepare sensors..)
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(Math.round((float) b.x), Math.round((float) b.y));
            b.foodDist = b.foodFinder.foodDist(b.food, Math.round((float) b.x), Math.round((float) b.y));
        }

        //write input registers
        for (SensorInput in : inputs) {
            vm.regs[reg++] = in.get();
        }

        vm.run();

        //use output values
        for (Body2d b : bodies) {
            //fetch, limit and scale outputs
            left = Math.max(0, Math.min(vm.regs[reg++], 65535)) / intScaleFactor;
            right = Math.max(0, Math.min(vm.regs[reg++], 65535)) / intScaleFactor;

            //move
            b.motor.move(left, right);

            //pickup wallsense before coordinates are clipped
            b.wallSense.sense();
        }
    }


    @Override
    public int getFitness() {
        return food;
    }


    public void incFood() {
        food++;
    }

}
