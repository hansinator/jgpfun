package jgpfun;

import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author Hansinator
 */
public abstract class BaseOrganism implements Comparable<BaseOrganism> {

    protected static final Random rnd = new SecureRandom();
    
    
    public abstract void live();

    public abstract int getFitness();
    
    @Override
    public int compareTo(BaseOrganism o) {
        return new Integer(o.getFitness()).compareTo(o.getFitness());
    }

}
