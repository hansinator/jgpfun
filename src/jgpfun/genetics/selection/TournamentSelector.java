/*
 */
package jgpfun.genetics.selection;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import jgpfun.life.BaseOrganism;

/**
 *
 * @author Hansinator
 */
public class TournamentSelector implements SelectionStrategy {

    Random rnd = new SecureRandom();

    final int tournamentSize;


    public TournamentSelector(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }


    @Override
    public BaseOrganism select(List<BaseOrganism> organisms) {
        int maxFit = -1;
        int size;
        BaseOrganism fittest = null;

        if (organisms.size() < tournamentSize) {
            size = organisms.size();
        } else {
            size = tournamentSize;
        }

        if (size == 0) {
            return null;
        }

        for (int i = 0; i < size; i++) {
            BaseOrganism candidate = organisms.get(rnd.nextInt(organisms.size()));

            if (candidate.getFitness() > maxFit) {
                maxFit = candidate.getFitness();
                fittest = candidate;
            }
        }

        return fittest;
    }

}
