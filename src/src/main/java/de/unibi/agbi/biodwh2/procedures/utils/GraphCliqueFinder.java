package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * Finds cliques in a given graph.
 */
public class GraphCliqueFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphCliqueFinder.class);

    /**
     * All cliques found while performing the clique finding algorithm
     */
    private ArrayList<ArrayList<Node>> cliques;
    /**
     * Target graph
     */
    private BaseGraph graph;

    public GraphCliqueFinder(final BaseGraph graph) {
        this.cliques = new ArrayList<ArrayList<Node>>();
        this.graph = graph;
        init(graph);
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
    private int findCliques(final BaseGraph graph, ArrayList<Node> potentialClique, ArrayList<Node> remaining, ArrayList<Node> skip, int depth) {

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

    /**
     * Finds all cliques detected by the Clique finder that contain a specific node
     * @param node Target node
     * @return List containing all cliques with target node
     */
    public ArrayList<ArrayList<Node>> getCliquesForNode(Node node) {
        ArrayList<ArrayList<Node>> cliquesForNode = new ArrayList<>();
        for(ArrayList<Node> clique : cliques) {
            System.out.println("size: " + clique.size());
            if(containsNode(node, clique)) {
                cliquesForNode.add(clique);
            }
        }
        return cliquesForNode;
    }

    /**
     * Check whether a list contains a node (auxiliary function)
     * @param node Target node
     * @param list List to check
     * @return Determines whether the list contains the node
     */
    private boolean containsNode(Node node, ArrayList<Node> list) {
        for(Node element : list) {
            if(element.getId().equals(node.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes clique detection for a given graph.
     * @param graph Graph to check
     */
    private void init(final BaseGraph graph) {
        LOGGER.info("Initializing clique detection ...");
        ArrayList<Node> nodesInitial = new ArrayList<>();
        // add all graph nodes to "remaining" list
        for(Node node : graph.getNodes()) {
            nodesInitial.add(node);
        }
        // start actual detection
        findCliques(graph, new ArrayList<>(), nodesInitial, new ArrayList<>(), 0);
        LOGGER.info(cliques.size() + " clique(s) found");
    }

    /**
     * Re-initializes the clique detection process for a new graph and clears old data.
     * @param graph New graph object
     */
    public void setGraph(final BaseGraph graph) {
        LOGGER.info("Clearing old clique data ...");
        cliques = new ArrayList<>();
        this.graph = graph;
        init(graph);
    }

    public ArrayList<ArrayList<Node>> getCliques() { return cliques; }

}
