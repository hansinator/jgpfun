package jgpfun;

import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author Hansinator
 */
public abstract class BaseOrganism implements Comparable<BaseOrganism> {

    protected static final Random rnd = new SecureRandom();

    protected final Genome genome;


    public BaseOrganism(Genome genome) {
        this.genome = genome;
    }


    public abstract void live();


    public abstract int getFitness();


    public Genome getGenome() {
        return genome;
    }


    @Override
    public int compareTo(BaseOrganism o) {
        return new Integer(this.getFitness()).compareTo(o.getFitness());
    }

}
