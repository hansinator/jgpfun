package jgpfun.testing;

import java.io.IOException;
import jgpfun.genetics.Genome;
import jgpfun.world2d.Organism2d;
import jgpfun.world2d.Body2d;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class OrganismDebug extends Organism2d {

    public boolean showdebug = false;

    //speed profiling helper vars
    //public long vmrun, allrun, comp;

    
    public OrganismDebug(Genome genome, World2d world) throws IOException {
        super(genome);
        addToWorld(world);
    }

/*
    //NOTE: this only works with precisionbodys!
    //old experimentally optimized live method from pc version
    @Override
    public void live() {
        int left, right, scale = 65535;//(int)((Integer.MAX_VALUE / (2.0*Math.PI)));
        double foodDist = 0.0, oldx = 0.0, oldy = 0.0;

        //long start2 = System.nanoTime();

        //write input registers
        int inreg = 0;
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(Math.round((float)b.x), Math.round((float)b.y));
            foodDist = b.foodFinder.foodDist(b.food, Math.round((float)b.x), Math.round((float)b.y));

            if (showdebug) {
                System.out.println("");
                System.out.println("Food dist " + foodDist);
                System.out.println("fooddistx " + (b.food.x - b.x));
                System.out.println("fooddisty " + (b.food.y - b.y));
                System.out.println("foodx " + (((b.food.x - b.x) / foodDist) * scale));
                System.out.println("foody " + (((b.food.y - b.y) / foodDist) * scale));
            }

            //cached cosdir and scale as int are meant to speed this up
            vm.regs[inreg++] = (int) (((PrecisionBody2d)b).cosdir * scale);
            vm.regs[inreg++] = (int) (((b.food.x - b.x) / foodDist) * scale);
            vm.regs[inreg++] = (int) (((b.food.y - b.y) / foodDist) * scale);
        }

        //long start = System.nanoTime();

        vm.run();

        //vmrun = (System.nanoTime() - start);
        //start = System.nanoTime();

        //use output values
        for (Body2d b : bodies) {
            //fetch and scale outputs
            left = vm.regs[6] / scale;
            right = vm.regs[7] / scale;

            if (showdebug) {
                oldx = b.x;
                oldy = b.y;
            }

            //move
            b.motor.move(left, right);

            if (showdebug) {
                System.out.println("");
                System.out.println("dirdelta: " + ((right - left) / 160000.0));
                //System.out.println("xdelta: " + ((Math.sin(b.dir) * speed) / 30000.0));
                //System.out.println("ydelta: " + ((b.cosdir * speed) / 30000.0));
                System.out.println("old x: " + oldx);
                System.out.println("old y: " + oldy);
                System.out.println("cur x: " + b.x);
                System.out.println("cur y: " + b.y);
            }
        }

        //comp = (System.nanoTime() - start);
        //allrun = (System.nanoTime() - start2);
    }
*/
}
