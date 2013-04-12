/*
 */

package de.hansinator.fun.jgp.genetics.selection;

import java.util.List;
import de.hansinator.fun.jgp.life.BaseOrganism;

/**
 *
 * @author Hansinator
 */
public interface SelectionStrategy {

    public BaseOrganism select(List<BaseOrganism> organisms, int totalFitness);
}
