/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.jgp.operations;

/**
 *
 * @author dahmen
 */
public class OpMax implements Operation {

    @Override
    public int execute(int src1, int src2) {
        return Math.max(src1, src2);
    }

}