package jgpfun.crossover;

import java.util.List;
import java.util.Random;
import jgpfun.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public class UniformCrossover implements CrossoverOperator {

    @Override
    public void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd) {
        //cutpoint, here it is the length of the smaller genome
        int cut = Math.min(parent1.size(), parent2.size());

        //some temporary variable for value swapping
        OpCode tmp;

        //distribute each gene either to the first or the second child
        for (int i = 0; i < cut; i++) {
            //50/50 chance to switch genes, rest stays same
            if (rnd.nextInt(100) >= 50) {
                tmp = parent1.get(i);
                parent1.set(i, parent2.get(i));
                parent2.set(i, tmp);
            }
        }
    }

}
