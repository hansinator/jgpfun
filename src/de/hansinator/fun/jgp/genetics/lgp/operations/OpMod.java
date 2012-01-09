package de.hansinator.fun.jgp.genetics.lgp.operations;

/**
 *
 * @author dahmen
 */
public class OpMod implements Operation {

    @Override
    public int execute(int src1, int src2) {
        if (src2 != 0) {
            return src1 % src2;
        } else {
            return Integer.MAX_VALUE;
        }
    }

}
