/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.jgp.operations;

/**
 *
 * @author dahmen
 */
public class OpSqrt implements Operation, UnaryOperation {

    public int execute(int src1, int src2) {
        return (int)Math.sqrt(src1);
    }

}