package de.hansinator.fun.jgp.genetics.lgp.operations;

/**
 *
 * @author dahmen
 */
public class OpAdd implements Operation {

    @Override
    public int execute(int src1, int src2) {
        return src1 + src2;
    }
}