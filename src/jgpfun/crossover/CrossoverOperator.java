package jgpfun.crossover;

import jgpfun.jgp.OpCode;

/**
 *
 * @author hansinator
 */
public interface CrossoverOperator
{
    //this function shall cross two parent genomes
    //and place the new ones back into the genomes
    void cross(OpCode[] parent1, OpCode[] parent2);
}
