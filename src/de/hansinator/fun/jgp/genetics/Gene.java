package de.hansinator.fun.jgp.genetics;


/*
 * XXX implement a GeneView class/interface and construct a geneview factory backed by a geneview<->gene.class mapping
 */
public interface Gene<T, E>
{
	public Gene<T, E> replicate();

	public void mutate();

	public T express(E context);
}
