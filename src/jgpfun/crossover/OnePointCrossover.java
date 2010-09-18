package jgpfun.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jgpfun.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public class OnePointCrossover implements CrossoverOperator {

    //combine the two genomes to produce two new ones
    //store the new genomes directly in parent1 and parent2
    @Override
    public void cross(OpCode[] parent1, OpCode[] parent2, Random rnd) {
            //copy source genomes, as we don't want our organisms
            //to telepathically share code portions!
            List<OpCode> in1 = Arrays.asList(parent1.clone());
            List<OpCode> in2 = Arrays.asList(parent2.clone());

            //target genomes are lists projected onto the source genomes
            //this way our list operations "write through" to the arrays
            List<OpCode> out1 = Arrays.asList(parent1);
            List<OpCode> out2 = Arrays.asList(parent2);

            //clear target genomes
            out1.clear();
            out2.clear();

            //cutpoint
            int cut = rnd.nextInt(Math.min(parent1.length, parent2.length));

            //add first halves
            out1.addAll(in1.subList(0, cut));
            out2.addAll(in2.subList(0, cut));

            //and the other halves from the other parent
            out1.addAll(in2.subList(cut, parent2.length));
            out2.addAll(in1.subList(cut, parent1.length));
        }

}
