package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Finds maximal cliques in a given graph.
 */
public class GraphCliqueFinder {

    /**
     * All cliques found while performing the clique finding algorithm
     */
    private ArrayList<ArrayList<Node>> cliques;
    /**
     * Target graph
     */
    private Graph graph;

    public GraphCliqueFinder(Graph graph) {
        this.cliques = new ArrayList<ArrayList<Node>>();
        this.graph = graph;
    }

    /**
     * Bron-Kerbosch algorithm for recursively finding cliques in an undirected graph.
     * @param graph Graph to analyze
     * @param potentialClique A list with nodes to process (initially: all nodes of the graph), potential members of a single clique
     * @param remaining Nodes that have not been processed yet
     * @param skip List of nodes that have already been processed, i.e. nodes that have already been in a clique (prevents finding the same clique twice)
     * @param depth Recursion depth
     * @return
     */
    public int findCliques(final Graph graph, ArrayList<Node> potentialClique, ArrayList<Node> remaining, ArrayList<Node> skip, int depth) {

        // all nodes have been processed -> new clique has been found
        if(remaining.isEmpty() && skip.isEmpty()) {
            ArrayList<Node> clique = new ArrayList<>();
            for(Node node : potentialClique) {
                clique.add(node);
            }
            cliques.add(clique);
            return 1;
        }

        int cliquesFound = 0;

        Iterator<Node> iterator = remaining.iterator();

        while(iterator.hasNext()) {

            // obtain node an neighbor list
            Node node = iterator.next();
            ArrayList<Node> neighbors = GraphProcedureUtils.getNeighbors(graph, node, GraphMode.UNDIRECTED);

            // add node to clique
            ArrayList<Node> newPotentialClique = potentialClique;
            newPotentialClique.add(node);

            // update list of remaining nodes
            ArrayList<Node> remainingNew = new ArrayList<>();
            for(Node nodeRemaining : remaining) {
                if(containsNode(nodeRemaining, neighbors)) {
                    remainingNew.add(nodeRemaining);
                }
            }

            // update list of nodes to skip
            ArrayList<Node> skipNew = new ArrayList<>();
            for(Node nodeSkip : skip) {
                if(containsNode(nodeSkip, neighbors)) {
                    skipNew.add(nodeSkip);
                }
            }

            // call algorithm with updates lists
            cliquesFound += findCliques(graph, newPotentialClique, remainingNew, skipNew, depth + 1);

            // node has been processed
            iterator.remove();
            potentialClique.remove(node);
            skip.add(node);

        }
        return cliquesFound;
    }

    public ArrayList<ArrayList<Node>> getCliquesForNode(long nodeID) {
        ArrayList<ArrayList<Node>> cliquesForNode = new ArrayList<>();
        for(ArrayList<Node> clique : cliques) {
            if(containsNode(graph.getNode(nodeID), clique)) {
                cliquesForNode.add(clique);
            }
        }
        return cliquesForNode;
    }

    private boolean containsNode(Node node, ArrayList<Node> list) {
        for(Node element : list) {
            if(element.getId().equals(node.getId())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ArrayList<Node>> getCliques() {
        return cliques;
    }
}
