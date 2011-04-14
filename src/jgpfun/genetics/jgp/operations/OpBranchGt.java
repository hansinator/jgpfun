/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgpfun.genetics.jgp.operations;

/**
 *
 * @author dahmen
 */
public class OpBranchGt implements Operation, BranchOperation {

    @Override
    public int execute(int src1, int src2) {
        if (src1 > src2) {
            return 1;
        }

        return 0;
    }
}
