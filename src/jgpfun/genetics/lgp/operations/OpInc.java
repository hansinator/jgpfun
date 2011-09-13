package jgpfun.genetics.lgp.operations;

/**
 *
 * @author hansinator
 */
public class OpInc implements Operation, UnaryOperation {

    @Override
    public int execute(int src1, int src2) {
        return src1+1;
    }

}
