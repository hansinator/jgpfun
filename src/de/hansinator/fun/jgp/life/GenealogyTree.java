/*
 */
package de.hansinator.fun.jgp.life;

import java.util.ArrayList;
import java.util.HashMap;
import de.hansinator.fun.jgp.genetics.Genome;

/**
 *
 * @author Hansinator
 */
public class GenealogyTree {

    private final HashMap<Genome, Node> nodes;

    private final ArrayList<Node> roots;


    //TODO: include functionality to pit two or more ants against each other
    //TODO: add mini-world-sim for single ant (or multiple? see above) with simple controls for testing organisms


    public GenealogyTree() {
        nodes = new HashMap<Genome, Node>();
        roots = new ArrayList<Node>();
    }

    public void clear() {
        roots.clear();
        nodes.clear();
    }


    public void put(Genome genome) {
        put(genome, null, 0);
    }


    public void put(Genome genome, Genome parent, int fitness) {
        if (!nodes.containsKey(genome) && ((parent == null) || nodes.containsKey(parent))) {
            new Node(genome, parent, fitness);
        }
    }

    //private final
    private class Node {

        public final Genome genome;

        public final Node parent;

        public final int fitness;

        private Node child = null;


        Node(Genome genome, Genome parent, int fitness) {
            this.genome = genome;
            this.fitness = fitness;
            if (parent == null) {
                this.parent = null;
                roots.add(this);
            } else {
                this.parent = nodes.get(parent);
                this.parent.child = this;
            }
        }


        public boolean hasChild() {
            return child != null;
        }


        public Node getChild() {
            return child;
        }

    }
}
