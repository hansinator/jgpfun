/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.jgp.operations;

/**
 *
 * @author Administrator
 */
public class OpInc implements Operation, UnaryOperation {

    public int execute(int src1, int src2) {
        return src1++;
    }

}
