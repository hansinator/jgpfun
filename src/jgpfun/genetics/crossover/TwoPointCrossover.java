package jgpfun.genetics.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jgpfun.genetics.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public class TwoPointCrossover implements CrossoverOperator {

    @Override
    public void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd) {
            //copy source genomes
            List<OpCode> in1 = new ArrayList(parent1.size()), in2 = new ArrayList(parent2.size());
            in1.addAll(parent1);
            in2.addAll(parent2);

            //clear target genomes
            parent1.clear();
            parent2.clear();

            //cutpoints
            int cut1 = rnd.nextInt(Math.min(in1.size(), in2.size()));
            int cut2 = rnd.nextInt(Math.min(in1.size(), in2.size()));

            //sort cutpoints
            int tmp = Math.min(cut1, cut2);
            cut2 = Math.max(cut1, cut2);
            cut1 = tmp;

            //crossover first genome
            parent1.addAll(in1.subList(0, cut1));
            parent1.addAll(in2.subList(cut1, cut2));
            parent1.addAll(in1.subList(cut2, in1.size()));

            //crossover second genome
            parent2.addAll(in2.subList(0, cut1));
            parent2.addAll(in1.subList(cut1, cut2));
            parent2.addAll(in2.subList(cut2, in2.size()));
        }

}
