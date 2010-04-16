package jgpfun;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.Organism;

/**
 *
 * @author hansinator
 */
public class PopulationManager {

    Random rnd;
    List<Organism> ants;
    List<Food> food;
    final int worldWidth, worldHeight;
    public static final int foodTolerance = 10;
    public static final int maxMutations = 2;
    public final int progSize;
    final Object lock = new Object();
    ThreadPoolExecutor pool;

    public PopulationManager(int worldWidth, int worldHeight, int popSize, int progSize, int foodCount) {
        ants = new ArrayList<Organism>(popSize);

        for (int i = 0; i < popSize; i++) {
            ants.add(Organism.randomOrganism(worldWidth, worldHeight, progSize));
        }

        rnd = new SecureRandom();

        food = new ArrayList<Food>(foodCount);
        for (int i = 0; i < foodCount; i++) {
            food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight)));
        }

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.progSize = progSize;

        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public double foodDist(Food f, int x, int y) {
        return Math.sqrt(((x - f.x) * (x - f.x)) + ((y - f.y) * (y - f.y)));
    }

    public Food findNearestFood(int x, int y) {
        double minDist = 1000000, curDist;
        int indexMinDist = -1;

        for (int i = 0; i < food.size(); i++) {
            curDist = foodDist(food.get(i), x, y);

            //limit visible range to 200
            //if (curDist > 200)
            //    continue;

            if (curDist < minDist) {
                minDist = curDist;
                indexMinDist = i;
            }
        }

        if (indexMinDist > -1) {
            return food.get(indexMinDist);
        } else {
            return new Food(x, y);
        }
    }

    static int gen = 0;
    public void runGeneration(int iterations, MainView mainView) {
        long start = System.currentTimeMillis();
        long time;

        for (int i = 0; i < iterations; i++) {
            step();
            if ((i % 400) == 0) {
                time = System.currentTimeMillis() - start;
                mainView.drawStuff(food, ants, time > 0 ? (int) ((i * 1000) / time) : 1);
                mainView.repaint();
            }
        }

        gen++;

        System.out.println("GEN: " + gen);
        System.out.println("RPS: " + ((iterations * 1000) / (System.currentTimeMillis() - start)));

        int foodCollected = newGeneration();

        System.out.println("Food collected: " + foodCollected);

        int fs = food.size();
        food = new ArrayList<Food>(fs);
        for (int i = 0; i < fs; i++) {
            food.add(new Food(rnd.nextInt(worldWidth), rnd.nextInt(worldHeight)));
        }
    }

    void step() {
        final CountDownLatch cb = new CountDownLatch(ants.size());

        for (final Organism organism : ants) {
            Runnable r = new Runnable() {
                public void run() {
                    //find closest food
                    Food f = findNearestFood(organism.x, organism.y);

                    try {
                        organism.live(f, foodDist(f, organism.x, organism.y));
                    } catch (Exception ex) {
                        Logger.getLogger(PopulationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //compute new movement here
                    //TODO: move computation from ant to here or somewhere else

                    //prevent world wrapping
                    //TODO: take into account ant size, so it can't hide outside of the screen
                    organism.x = Math.min(Math.max(organism.x, 0), worldWidth);
                    organism.y = Math.min(Math.max(organism.y, 0), worldHeight);

                    //eat food
                    synchronized (lock) {
                        if ((food.contains(f)) &&
                                (f.x >= (organism.x - foodTolerance)) &&
                                (f.x <= (organism.x + foodTolerance)) &&
                                (f.y >= (organism.y - foodTolerance)) &&
                                (f.y <= (organism.y + foodTolerance))) {
                            organism.food++;
                            f.x = rnd.nextInt(worldWidth);
                            f.y = rnd.nextInt(worldHeight);
                        }
                    }

                    cb.countDown();
                }                
            };

            pool.execute(r);
        }
        try {
            cb.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(PopulationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int newGeneration() {
        int totalFit = calculateFitness();
        OpCode[] parent1, parent2;
        double mutador;
        List<Organism> newAnts = new ArrayList<Organism>(ants.size());

        //choose crossover operator
        //CrossoverOperator crossOp = new TwoPointCrossover();

        //create new genomes via cloning and mutation or crossover
        for (int i = 0; i < (ants.size() / 2); i++) {
            //select two source genomes and clone them
            //note: you must copy/clone the genomes before modifying them,
            //as the genome is passed by reference
            parent1 = rouletteWheel(totalFit).clone();
            parent2 = rouletteWheel(totalFit).clone();

            //mutate or crossover with a user defined chance
            //mutador = rnd.NextDouble();
            //if (mutador > crossoverRate) {
            //mutate genomes
            parent1 = mutate(parent1, rnd.nextInt(maxMutations) + 1);
            parent2 = mutate(parent2, rnd.nextInt(maxMutations) + 1);
            /*} //crossover
            else {
            //perform crossover
            //(crossover operators automatically copy the genomes)
            crossOp.Cross(parent1, parent2, randomR);
            }*/

            //create new ants with the modified genomes and save them
            newAnts.add(new Organism(parent1, rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), rnd.nextDouble()));
            newAnts.add(new Organism(parent2, rnd.nextInt(worldWidth), rnd.nextInt(worldHeight), rnd.nextDouble()));
        }

        //replace and leave the other to GC
        ants = newAnts;

        return totalFit;
    }

    //fintess proportionate selection
    private Organism rouletteWheel(int totalFit) {
        int stopPoint = 0;
        int fitnessSoFar = 0;

        if (totalFit > 0) {
            stopPoint = rnd.nextInt(totalFit);
        }

        for (int i = 0; i < ants.size(); i++) {
            fitnessSoFar += ants.get(i).food;
            //this way zero fitness ants are omitted
            if (fitnessSoFar > stopPoint) {
                return ants.get(i);
            }
        }

        return ants.get(rnd.nextInt(ants.size()));
    }

    //make random changes to random locations in the genome
    private OpCode[] mutate(OpCode[] genome, int mutCount) {
        //determine amount of mutations, minimum 1
        //int mutCount = maxMutations;
        //int mutCount = randomR.Next(maxMutations) + 1;

        for (int i = 0; i < mutCount; i++) {
            genome = mutateProgramSpace(genome);
        }

        return genome;
    }

    //the team effort
    private int calculateFitness() {
        int totalFit = 0;
        for (Organism o : ants) {
            totalFit += o.food;
        }
        return totalFit;

    }
    final int maxRegisterValDelta = 16;
    final int maxConstantValDelta = 16384;

    public OpCode[] mutateProgramSpace(OpCode[] program) {
        List<OpCode> programSpace = new ArrayList(program.length);
        programSpace.addAll(Arrays.asList(program));
        //fetch programspace and weights

        //define chances for what mutation could happen in some sort of percentage
        int mutateIns = 22, mutateRem = 18, mutateRep = 20, mutateVal = 20;
        int mutateSrc2 = 20, mutateTrg = 20, mutateOp = 20, mutateFlags = 20;
        //chances sum represents 100%, i.e. the sum of all possible chances
        int chancesSum;
        //the choice of mutation
        int mutationChoice;
        //choose random location
        int loc = rnd.nextInt(program.length);
        //precalculate a random value, but exclude zero
        //zero is no valid constant, as it tends to create semantic introns
        //(like a = a + 0 or b = n * 0 and so on)
        int val = rnd.nextInt();
        //while (val == 0)
        //{
        //    val = rnd.Next(Int32.MinValue, Int32.MaxValue);
        //}

        OpCode instr = programSpace.get(loc);

        //now see what to do
        //either delete an opcode, add a new or mutate an existing one

        //first determine which mutations are possible and add up all the chances
        //if we have the max possible opcodes, we can't add a new one
        if (programSpace.size() >= progSize) {
            mutateIns = 0;
        }

        //if we have only 4 opcodes left, don't delete more
        if (programSpace.size() < 5) {
            mutateRem = 0;
        }

        //replacement is always possible..
        //add all up
        chancesSum = mutateRep + mutateIns + mutateRem + mutateVal + mutateSrc2 + mutateTrg + mutateOp + mutateFlags;

        //choose mutation
        mutationChoice = rnd.nextInt(chancesSum);

        //see which one has been chosen
        //mutate ins
        if (mutationChoice < mutateIns) {
            //insert a random instruction at a random location
            programSpace.add(loc, OpCode.randomOne(rnd));
        } //mutate rem
        else if (mutationChoice < (mutateIns + mutateRem)) {
            //remove a random instruction
            programSpace.remove(loc);
        } //mutate rep
        else if (mutationChoice < (mutateIns + mutateRem + mutateRep)) {
            //replace a random instruction
            programSpace.set(loc, OpCode.randomOne(rnd));
        } //mutate src1 or immediate value
        else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal)) {
            //if immediate, modify the constant value by random value
            if (instr.immediate) {
                val = rnd.nextInt(maxConstantValDelta * 2) - maxConstantValDelta;
                instr.src1 += val;
            } //else modify the src1 register number by a random value
            else {
                val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
                instr.src1 += val;
            }

            //save modified instruction
            programSpace.set(loc, instr);
        } //mutate src2
        else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2)) {
            //modify the src2 register number by a random value
            val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
            instr.src2 = (instr.src2 + val);

            //save modified instruction
            programSpace.set(loc, instr);
        } //mutate trg
        else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2 + mutateTrg)) {
            //modify trg field by random value
            //(the scale of the value might be ridiculous...)
            //do
            //{
            val = rnd.nextInt(maxRegisterValDelta * 2) - maxRegisterValDelta;
            //modify and normalize
            instr.trg = (instr.trg + val);
            //}
            //don't write input registers
            //while (!((instr.trg == 4) || (instr.trg == 5)));

            //save modified instruction
            programSpace.set(loc, instr);
        } //mutate op
        else if (mutationChoice < (mutateIns + mutateRem + mutateRep + mutateVal + mutateSrc2 + mutateTrg + mutateOp)) {
            //replace opcode field by random value
            instr.op = rnd.nextInt();

            //save modified instruction
            programSpace.set(loc, instr);
        } //mutate opflags
        else {
            //set new random opflags
            instr.immediate = rnd.nextBoolean();

            //save modified instruction
            programSpace.set(loc, instr);
        }

        program = new OpCode[programSpace.size()];
        return programSpace.toArray(program);
    }
}
