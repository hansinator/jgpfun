package jgpfun.world2d;

import java.awt.Color;
import java.awt.Graphics;
import jgpfun.world2d.senses.WallSense;
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

    public final FoodAntBody[] bodies;

    private final SensorInput[] inputs;

    private int food;


    public Organism2d(Genome genome) throws IOException {
        super(genome);
        this.vm = new EvoVM(registerCount, genome.program.toArray(new OpCode[genome.program.size()]));
        //this.vm = EvoCompiler.compile(registerCount, genome.program.toArray(new OpCode[genome.program.size()]));
        
        this.food = 0;
        this.bodies = new FoodAntBody[1];
        this.inputs = new SensorInput[7 * bodies.length];
    }


    public void addToWorld(World2d world) {
        //init bodies and grab inputs
        int x = 0;
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new FoodAntBody(this, world);
            for (SensorInput input : bodies[i].getInputs()) {
                inputs[x++] = input;
            }

            bodies[i].x = rnd.nextInt(world.worldWidth);
            bodies[i].y = rnd.nextInt(world.worldHeight);
            bodies[i].dir = rnd.nextDouble() * 2 * Math.PI;
        }
    }


    @Override
    public void live() {
        double left, right;
        int reg = 0;

        //calculate food stuff for body (prepare sensors..)
        for (Body2d b : bodies) {
            b.prepareInputs();
        }

        //write input registers
        for (SensorInput in : inputs) {
            vm.regs[reg++] = in.get();
        }

        vm.run();

        //use output values
        for (FoodAntBody b : bodies) {
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


    public void draw(Graphics g) {
        g.setColor(Color.red);

        for (Body2d b : bodies) {
            double sindir = Math.sin(b.dir);
            double cosdir = Math.cos(b.dir);
            int xrot = (int) Math.round(8.0 * sindir);
            int yrot = (int) Math.round(8.0 * cosdir);
            int x = Math.round((float) b.x);
            int y = Math.round((float) b.y);

            g.drawLine(x, y, x + xrot, y - yrot);
            g.fillOval(x, y, 4, 4);

            //Polygon p = new Polygon();
                /*
            //1st try
            p.addPoint((b.x - xrot), (b.y + yrot));
            p.addPoint((b.x + xrot), (b.y + yrot));
            p.addPoint(b.x + xrot, b.y - 6 + yrot);

            //2nd try - looks like a triangle
            p.addPoint((b.x - 4 + xrot), (b.y + 4 + yrot));
            p.addPoint((b.x + 4 + xrot), (b.y + 4 + yrot));
            p.addPoint(b.x + xrot, b.y - 6 + yrot);

            g.drawPolygon(p);
            g.fillPolygon(p);*/
        }

    }

}
