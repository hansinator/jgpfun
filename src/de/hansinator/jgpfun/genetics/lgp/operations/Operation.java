/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hansinator.jgpfun.genetics.lgp.operations;

import java.io.Serializable;

/**
 *
 * @author dahmen
 */
public interface Operation extends Serializable {
    public int execute(int src1, int src2);
}
