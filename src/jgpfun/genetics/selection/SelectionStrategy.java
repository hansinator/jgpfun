/*
 */

package jgpfun.genetics.selection;

import java.util.List;
import jgpfun.BaseOrganism;

/**
 *
 * @author Hansinator
 */
public interface SelectionStrategy {

    public BaseOrganism select(List<BaseOrganism> organisms);
}
