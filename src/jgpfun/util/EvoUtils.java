package jgpfun.util;

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
        }

        for (int i = 0; i < ants.size(); i++) {
            fitnessSoFar += ants.get(i).food;
            //this way zero fitness ants are omitted
            if (fitnessSoFar > stopPoint) {
                return ants.get(i);
            }
        }

        return ants.get(rnd.nextInt(ants.size()));
    }

}
