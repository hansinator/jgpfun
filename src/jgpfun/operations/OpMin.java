/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.operations;

/**
 *
 * @author dahmen
 */
public class OpMin implements Operation {

    public int execute(int src1, int src2) {
        return Math.min(src1, src2);
    }

}
