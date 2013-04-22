/*
 */
package de.hansinator.fun.jgp.life;

import java.util.ArrayList;
import java.util.HashMap;

import de.hansinator.fun.jgp.genetics.AntGenome;

/**
 * 
 * @author Hansinator
 */
public class GenealogyTree
{

	private final HashMap<AntGenome, Node> nodes;

	private final ArrayList<Node> roots;

	// TODO: include functionality to pit two or more ants against each other
	// TODO: add mini-world-sim for single ant (or multiple? see above) with
	// simple controls for testing organisms

	public GenealogyTree()
	{
		nodes = new HashMap<AntGenome, Node>();
		roots = new ArrayList<Node>();
	}

	public void clear()
	{
		roots.clear();
		nodes.clear();
	}

	public void put(AntGenome genome)
	{
		put(genome, null, 0);
	}

	public void put(AntGenome genome, AntGenome parent, int fitness)
	{
		if (!nodes.containsKey(genome) && ((parent == null) || nodes.containsKey(parent)))
			new Node(genome, parent, fitness);
	}

	// private final
	private class Node
	{

		public final AntGenome genome;

		public final Node parent;

		public final int fitness;

		private Node child = null;

		Node(AntGenome genome, AntGenome parent, int fitness)
		{
			this.genome = genome;
			this.fitness = fitness;
			if (parent == null)
			{
				this.parent = null;
				roots.add(this);
			} else
			{
				this.parent = nodes.get(parent);
				this.parent.child = this;
			}
		}

		public boolean hasChild()
		{
			return child != null;
		}

		public Node getChild()
		{
			return child;
		}

	}
}
