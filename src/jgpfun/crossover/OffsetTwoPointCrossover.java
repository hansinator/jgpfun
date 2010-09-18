package jgpfun.crossover;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jgpfun.jgp.OpCode;

/**
 * Basically the same as TwoPointCrossover, only that the exchanged
 * part is not at the exact same position in both genomes, but offset
 * by a random value
 *
 * @author hansinator
 */
public class OffsetTwoPointCrossover implements CrossoverOperator {

    @Override
    public void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd) {
        //copy source genomes
        List<OpCode> in1 = Arrays.asList(parent1.toArray(new OpCode[parent1.size()]));
        List<OpCode> in2 = Arrays.asList(parent2.toArray(new OpCode[parent2.size()]));

        //clear target genomes
        parent1.clear();
        parent2.clear();

        //exchanged part width and offset in each target genome
        int width = rnd.nextInt(Math.min(in1.size(), in2.size()));
        int off1 = rnd.nextInt(in1.size() - width);
        int off2 = rnd.nextInt(in2.size() - width);

        //crossover first genome
        parent1.addAll(in1.subList(0, off1));
        parent1.addAll(in2.subList(off2, off2 + width));
        parent1.addAll(in1.subList(off1 + width, in1.size()));

        //crossover second genome
        parent2.addAll(in2.subList(0, off2));
        parent2.addAll(in1.subList(off1, off1 + width));
        parent2.addAll(in2.subList(off2 + width, in2.size()));
    }

}
