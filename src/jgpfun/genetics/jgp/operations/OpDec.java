/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.genetics.jgp.operations;

/**
 *
 * @author Administrator
 */
public class OpDec implements Operation, UnaryOperation {

    @Override
    public int execute(int src1, int src2) {
        return src1--;
    }

}
