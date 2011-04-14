/*
 */

package jgpfun.genetics.selection;

import java.util.List;
import jgpfun.world2d.Organism2d;

/**
 *
 * @author Hansinator
 */
public interface SelectionStrategy {

    public Organism2d select(List<Organism2d> organisms);
}
