package jgpfun.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd) {
            //copy source genomes, as we don't want our organisms
            //to telepathically share code portions!
            List<OpCode> in1 = Arrays.asList(parent1.toArray(new OpCode[parent1.size()]).clone());
            List<OpCode> in2 = Arrays.asList(parent2.toArray(new OpCode[parent2.size()]).clone());

            //clear target genomes
            parent1.clear();
            parent2.clear();

            //cutpoint
            int cut = rnd.nextInt(Math.min(in1.size(), in2.size()));

            //add first halves of the same parent
            parent1.addAll(in1.subList(0, cut));
            parent2.addAll(in2.subList(0, cut));

            //and the other halves from the other parent
            parent1.addAll(in2.subList(cut, in2.size()));
            parent2.addAll(in1.subList(cut, in1.size()));
        }

}
