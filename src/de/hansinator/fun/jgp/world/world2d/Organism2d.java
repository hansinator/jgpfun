package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.IOException;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;
import de.hansinator.fun.jgp.util.Settings;

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

    static final int registerCount = Settings.getInt("registerCount");

    public static final double intScaleFactor = Settings.getDouble("intScaleFactor");

    public final BaseMachine vm;

    public final Body2d[] bodies;

    private final SensorInput[] inputs;
    
    private final ActorOutput[] outputs;

    private int food;


    public Organism2d(Genome genome) throws IOException {
        super(genome);
        this.food = 0;
        this.bodies = new Body2d[1];
        this.inputs = new SensorInput[RadarAntBody.getNumInputs() * bodies.length];
        this.outputs = new ActorOutput[RadarAntBody.getNumOutputs() * bodies.length];
        
        this.vm = new EvoVM(registerCount, this.inputs.length, genome.program.toArray(new OpCode[genome.program.size()]));
        //this.vm = EvoCompiler.compile(registerCount, this.inputs.length, genome.program.toArray(new OpCode[genome.program.size()]));
    }


    public void addToWorld(World2d world) {
    	int i = 0, o = 0;
    	
        //init bodies and grab inputs
        for (int x = 0; x < bodies.length; x++) {
        	//create body
            bodies[x] = new RadarAntBody(this, world);
            
            //collect inputs
            for (SensorInput in : bodies[x].getInputs()) {
                inputs[i++] = in;
            }
            
            //collect outputs
            for (ActorOutput out : bodies[x].getOutputs()) {
                outputs[o++] = out;
            }


            bodies[i].x = rnd.nextInt(world.worldWidth);
            bodies[i].y = rnd.nextInt(world.worldHeight);
            bodies[i].dir = rnd.nextDouble() * 2 * Math.PI;
        }
    }


    @Override
    public void live() {
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

        //write output values
        for (ActorOutput out : outputs) {
            out.set(vm.regs[reg++]);
        }
        
        //apply outputs (move motor etc)
        for (Body2d b : bodies) {
            b.applyOutputs();
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
        for (Body2d b : bodies) {
            b.draw(g);
        }
    }

}
