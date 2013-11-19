package de.hansinator.fun.jgp.genetics;

import java.util.List;


/*
 * XXX implement a GeneView class/interface and construct a geneview factory backed by a geneview<->gene.class mapping
 */
public interface Gene<T, E>
{
	Gene<T, E> replicate();
	
	void mutate();

	T express(E context);
	
	List<Gene<?,?>> getChildren();
	
	void setMutationChance(int mutationChance);
	
	int getMutationChance();
}
