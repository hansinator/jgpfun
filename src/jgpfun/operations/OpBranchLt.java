/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.operations;


/**
 *
 * @author dahmen
 */
public class OpBranchLt implements Operation, BranchOperation {

    public int execute(int src1, int src2) {
        if(src1 < src2) {
            return 1;
        }

        return 0;
    }

}
