/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.operations;

import jgpfun.Branch;
import jgpfun.NoOp;


/**
 *
 * @author dahmen
 */
public class OpBranchLt implements Operation {

    public int execute(int src1, int src2) {
        if(src1 < src2) {
            throw new NoOp();
        }

        throw new Branch();
    }

}
