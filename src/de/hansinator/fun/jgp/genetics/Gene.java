package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.Organism;

/*
 * XXX implement a GeneView class/interface and construct a geneview factory backed by a geneview<->gene.class mapping
 */
public interface Gene<T>
{
	public Gene<T> replicate();

	public T express(Organism organism);
}
