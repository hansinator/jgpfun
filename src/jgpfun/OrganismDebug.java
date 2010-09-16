package jgpfun;

import jgpfun.jgp.OpCode;
import jgpfun.world2d.Body2d;
import jgpfun.world2d.FoodFinder;

/**
 *
 * @author hansinator
 */
public class OrganismDebug extends Organism {

    public boolean showdebug = false;

    //speed profiling helper vars
    //public long vmrun, allrun, comp;
    //precision movement, should be recatored into some kind of body
    double dx, dy, cosdir;

    //PC - keep this for movement reasons
    /* only the important parts remain, vm stuff stripped
    public Organism(int x, int y, double dir) {
    dx = x;
    dy = y;
    this.dir = dir;
    cosdir = Math.cos(dir);
    }*/

    public OrganismDebug(OpCode[] program, int worldWidth, int worldHeight, FoodFinder foodFinder) {
        super(program, worldWidth, worldHeight, foodFinder);
    }


    //old experimentally optimized live method from pc version
    //TODO: refactor the movement related things into extra classes
    @Override
    public void live() throws Exception {
        int left, right, scale = 65535;//(int)((Integer.MAX_VALUE / (2.0*Math.PI)));
        double speed, foodDist = 0.0;

        //long start2 = System.nanoTime();

        //write input registers
        int inreg = 0;
        for (Body2d b : bodies) {
            b.food = b.foodFinder.findNearestFood(b.x, b.y);
            foodDist = b.foodFinder.foodDist(b.food, b.x, b.y);

            if (showdebug) {
                System.out.println("");
                System.out.println("Food dist " + foodDist);
                System.out.println("fooddistx " + (b.food.x - b.x));
                System.out.println("fooddisty " + (b.food.y - b.y));
                System.out.println("foodx " + (((b.food.x - b.x) / foodDist) * scale));
                System.out.println("foody " + (((b.food.y - b.y) / foodDist) * scale));
            }

            vm.regs[inreg++] = (int) (Math.cos(b.dir) * scale);
            vm.regs[inreg++] = (int) (((b.food.x - b.x) / foodDist) * scale);
            vm.regs[inreg++] = (int) (((b.food.y - b.y) / foodDist) * scale);
        }

        //global cosdir and scale as int are meant to speed this up
        vm.regs[0] = (int) (cosdir * scale);
        vm.regs[1] = (int) (cosdir * scale);

        //long start = System.nanoTime();

        vm.run();

        //vmrun = (System.nanoTime() - start);
        //start = System.nanoTime();

        //use output values
        for (Body2d b : bodies) {
            //fetch and scale outputs
            left = vm.regs[6] / scale;
            right = vm.regs[7] / scale;

            /*//fetch, limit and scale outputs
            left = Math.max(0, Math.min(vm.regs[3], 65535)) / scale;
            right = Math.max(0, Math.min(vm.regs[4], 65535)) / scale;*/

            //compute movement here
            //dir += (right - left) * (maxForce / 50000);   //find the direction
            b.dir += ((right - left) / 160000.0);   //find the direction
            speed = (right + left) / 2;
            cosdir = Math.cos(b.dir);

            if (showdebug) {
                System.out.println("");
                System.out.println("dirdelta: " + ((right - left) / 160000.0));
                System.out.println("xdelta: " + ((Math.sin(b.dir) * speed) / 30000.0));
                System.out.println("ydelta: " + ((cosdir * speed) / 30000.0));
                System.out.println("cur x: " + b.x);
                System.out.println("cur y: " + b.y);
            }

            //don't use a motor yet, apply the movement directly to the body
            /*x += Math.sin(dir) * maxSpeed * speed / 20000;       //max speed is just a twaking parameter; don't get confused by it
            y -= cosdir * maxSpeed * speed / 20000;       //try varying it in simulation*/

            //TODO: dx and dy are not body specific yet
            dx += ((Math.sin(b.dir) * speed) / 30000.0);
            dy -= ((cosdir * speed) / 30000.0);
            b.x = (int) dx;
            b.y = (int) dy;

            if (showdebug) {
                System.out.println("cur2 x: " + b.x);
                System.out.println("cur2 y: " + b.y);
            }
        }

        //comp = (System.nanoTime() - start);
        //allrun = (System.nanoTime() - start2);
    }

}
