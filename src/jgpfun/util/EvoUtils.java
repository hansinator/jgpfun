package jgpfun.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import jgpfun.Organism;

/**
 *
 * @author hansinator
 */
public class EvoUtils {

    //fitness proportionate selection
    public static Organism rouletteWheel(List<Organism> ants, int totalFit, Random rnd) {
        int stopPoint = 0;
        int fitnessSoFar = 0;

        if (totalFit > 0) {
            stopPoint = rnd.nextInt(totalFit);
        } else {
            return ants.get(rnd.nextInt(ants.size()));
        }

        /*
         * Shuffle the organism list to make roulettewheel work better.
         * In case this method is called multiple times on the same list,
         * the same organisms with a huge fitness values at the beginning
         * of the list would have a greater chance of being selected.
         * This shuffle hopefully eliminates this problem, if it does exist.
         */
        Collections.shuffle(ants);

        for (int i = 0; i < ants.size(); i++) {
            fitnessSoFar += ants.get(i).getFitness();
            //this way zero fitness ants are omitted
            if (fitnessSoFar > stopPoint) {
                return ants.get(i);
            }
        }

        return ants.get(rnd.nextInt(ants.size()));
    }

    
    //tournament selection
    public static Organism tournament(List<Organism> ants, int size, Random rnd) {
        int maxFit = -1;
        Organism fittest = null;

        if((size == 0) || (ants.size() < size))
            return null;

        for (int i = 0; i < size; i++) {
            Organism candidate = ants.get(rnd.nextInt(ants.size()));

            if(candidate.getFitness() > maxFit) {
                maxFit = candidate.getFitness();
                fittest = candidate;
            }
        }

        return fittest;
    }
}
