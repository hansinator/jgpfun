/*
 */

package de.hansinator.jgpfun.genetics.selection;

import java.util.List;
import de.hansinator.jgpfun.life.BaseOrganism;

/**
 *
 * @author Hansinator
 */
public interface SelectionStrategy {

    public BaseOrganism select(List<BaseOrganism> organisms);
}
