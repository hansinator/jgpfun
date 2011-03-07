package jgpfun;

import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author Hansinator
 */
public abstract class BaseOrganism implements Comparable<BaseOrganism> {

    public abstract int getFitness();

    protected static final Random rnd = new SecureRandom();
    
    @Override
    public int compareTo(BaseOrganism o) {
        return new Integer(o.getFitness()).compareTo(o.getFitness());
    }

}
