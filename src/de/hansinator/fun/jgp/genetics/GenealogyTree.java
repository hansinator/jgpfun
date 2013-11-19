/*
 */
package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.HashMap;

import de.hansinator.fun.jgp.life.OrganismGene;


/**
 * 
 * @author Hansinator
 */
public class GenealogyTree
{

	private final HashMap<OrganismGene, Node> nodes;

	private final ArrayList<Node> roots;

	// TODO: include functionality to pit two or more ants against each other
	// TODO: add mini-world-sim for single ant (or multiple? see above) with
	// simple controls for testing organisms

	public GenealogyTree()
	{
		nodes = new HashMap<OrganismGene, Node>();
		roots = new ArrayList<Node>();
	}

	public void clear()
	{
		roots.clear();
		nodes.clear();
	}

	public void put(OrganismGene genome)
	{
		put(genome, null);
	}

	public void put(OrganismGene genome, OrganismGene parent)
	{
		if (!nodes.containsKey(genome) && ((parent == null) || nodes.containsKey(parent)))
			new Node(genome, parent);
	}

	// private final
	private class Node
	{

		public final OrganismGene genome;

		public final Node parent;

		private Node child = null;

		Node(OrganismGene genome, OrganismGene parent)
		{
			this.genome = genome;
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
