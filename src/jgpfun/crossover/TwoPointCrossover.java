package jgpfun.crossover;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jgpfun.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public class TwoPointCrossover implements CrossoverOperator {

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

            //cutpoints
            int cut1 = rnd.nextInt(Math.min(parent1.length, parent2.length));
            int cut2 = rnd.nextInt(Math.min(parent1.length, parent2.length));

            //sort cutpoints
            int tmp = Math.min(cut1, cut2);
            cut2 = Math.max(cut1, cut2);
            cut1 = tmp;

            //crossover first genome
            out1.addAll(in1.subList(0, cut1));
            out1.addAll(in2.subList(cut1, cut2));
            out1.addAll(in1.subList(cut2, parent1.length));

            //crossover second genome
            out2.addAll(in2.subList(0, cut1));
            out2.addAll(in1.subList(cut1, cut2));
            out2.addAll(in2.subList(cut2, parent2.length));
        }

}
