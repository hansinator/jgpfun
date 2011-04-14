/*
 */
package jgpfun.selection;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import jgpfun.AbstractPopulationManager;
import jgpfun.world2d.Organism2d;

/**
 *
 * @author Hansinator
 */
public class RouletteWheelSelector implements SelectionStrategy {

    final AbstractPopulationManager populationManager;

    final Random rnd = new SecureRandom();


    public RouletteWheelSelector(AbstractPopulationManager populationManager) {
        this.populationManager = populationManager;
    }


    /**
     * fitness proportionate selection
     */
    @Override
    public Organism2d select(List<Organism2d> organisms) {
        int stopPoint = 0;
        int fitnessSoFar = 0;
        final int totalFit = populationManager.getCurrentPopulationFitness();

        if (totalFit > 0) {
            stopPoint = rnd.nextInt(totalFit);
        } else {
            return organisms.get(rnd.nextInt(organisms.size()));
        }

        /*
         * Shuffle the organism list to make roulettewheel work better.
         * In case this method is called multiple times on the same list,
         * the same organisms with a huge fitness values at the beginning
         * of the list would have a greater chance of being selected.
         * This shuffle hopefully eliminates this problem, if it does exist.
         */
        Collections.shuffle(organisms);

        for (int i = 0; i < organisms.size(); i++) {
            fitnessSoFar += organisms.get(i).getFitness();
            //this way zero fitness ants are omitted
            if (fitnessSoFar > stopPoint) {
                return organisms.get(i);
            }
        }

        return organisms.get(rnd.nextInt(organisms.size()));
    }

}
