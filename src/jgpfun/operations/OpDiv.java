/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.operations;

/**
 *
 * @author dahmen
 */
public class OpDiv implements Operation {

    public int execute(int src1, int src2) {
        if (src2 != 0) {
            return src1 / src2;
        } else {
            return Integer.MAX_VALUE;
        }
    }

}
