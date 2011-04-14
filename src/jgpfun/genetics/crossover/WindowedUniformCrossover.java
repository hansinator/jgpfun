package jgpfun.genetics.crossover;

import java.util.List;
import java.util.Random;
import jgpfun.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public class WindowedUniformCrossover implements CrossoverOperator {

    @Override
    public void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd) {
        //some temporary variables for value swapping
        OpCode opCode;
        int tmp;

        //cutpoints
        int cut1 = rnd.nextInt(Math.min(parent1.size(), parent2.size()));
        int cut2 = rnd.nextInt(Math.min(parent1.size(), parent2.size()));

        //sort cutpoints
        tmp = Math.min(cut1, cut2);
        cut2 = Math.max(cut1, cut2);
        cut1 = tmp;

        //distribute each gene either to the first or the second child,
        //but only within the selected window
        for (int i = cut1; i < cut2; i++) {
            //50/50 chance to switch genes, rest stays same
            if (rnd.nextInt(100) >= 50) {
                opCode = parent1.get(i);
                parent1.set(i, parent2.get(i));
                parent2.set(i, opCode);
            }
        }
    }

}
