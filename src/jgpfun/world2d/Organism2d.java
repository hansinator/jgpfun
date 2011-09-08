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

        bodies = new Body2d[1];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body2d(0.0, 0.0, rnd.nextDouble() * 2 * Math.PI);
        }

        inputs = new SensorInput[7 * bodies.length];
        int input = 0;
        for (final Body2d b : bodies) {
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    //cached cosdir and scale as int are meant to speed this up
                    //vm.regs[inreg++] = (int) (((PrecisionBody2d) b).cosdir * scale);
                    return (int) (Math.cos(b.dir) * intScaleFactor);
                }

            };
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    return (int) (((b.food.x - b.x) / b.foodDist) * intScaleFactor);
                }

            };
            ;
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    return (int) (((b.food.y - b.y) / b.foodDist) * intScaleFactor);
                }

            };
            ;
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    return (int) (b.foodDist * intScaleFactor);
                }

            };
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    return Math.round((float) b.foodDist);
                }

            };
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    return (int) (b.lastSpeed * intScaleFactor);
                }

            };
            inputs[input++] = new SensorInput() {

                @Override
                public int get() {
                    //wallsense
                    return b.wallSense.lastSenseVal;
                }

            };
        }
    }


    public void addToWorld(World2d world) {
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].x = rnd.nextInt(world.worldWidth);
            bodies[i].y = rnd.nextInt(world.worldHeight);
            bodies[i].foodFinder = world.foodFinder;
            bodies[i].wallSense = new WallSense(0, 0);
            bodies[i].wallSense.setBody(bodies[i]);
        }
    }


    @Override
    public void live() {
        double left, right;

        //calculate food stuff for body (prepare sensors..)
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(Math.round((float) b.x), Math.round((float) b.y));
            b.foodDist = b.foodFinder.foodDist(b.food, Math.round((float) b.x), Math.round((float) b.y));
        }

        //write input registers
        int inreg = 0;
        for (SensorInput in : inputs) {
            vm.regs[inreg++] = in.get();
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


    @Override
    public int getFitness() {
        return food;
    }


    public void incFood() {
        food++;
    }

}
