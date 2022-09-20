package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Finds cliques in a given graph.
 */
public class GraphCliqueFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphCliqueFinder.class);

    /**
     * All cliques found while performing the clique finding algorithm
     */
    private List<List<Long>> cliques;
    /**
     * Target graph
     */
    private BaseGraph graph;

    public GraphCliqueFinder(final BaseGraph graph) {
        this.cliques = new ArrayList<>();
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
    private int findCliques(final BaseGraph graph, final List<Long> potentialClique, final List<Long> remaining, final List<Long> skip, final int depth) {

        // all nodes have been processed -> new clique has been found
        if(remaining.isEmpty() && skip.isEmpty()) {
            final List<Long> clique = new ArrayList<>(potentialClique);
            cliques.add(clique);
            return 1;
        }

        int cliquesFound = 0;

        final Iterator<Long> iterator = remaining.iterator();

        while(iterator.hasNext()) {

            // obtain node an neighbor list
            final Long node = iterator.next();
            final List<Long> neighbors = GraphProcedureUtils.getNeighbors(graph, node, GraphMode.UNDIRECTED);

            // add node to clique
            List<Long> newPotentialClique = potentialClique;
            newPotentialClique.add(node);

            // update list of remaining nodes
            final List<Long> remainingNew = new ArrayList<>();
            for(final Long nodeRemaining : remaining) {
                if(containsNode(nodeRemaining, neighbors)) {
                    remainingNew.add(nodeRemaining);
                }
            }

            // update list of nodes to skip
            final List<Long> skipNew = new ArrayList<>();
            for(final Long nodeSkip : skip) {
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
     * @param nodeId Target node
     * @return List containing all cliques with target node
     */
    public List<List<Long>> getCliquesForNodeId(final Long nodeId) {
        final List<List<Long>> cliquesForNode = new ArrayList<>();
        for(final List<Long> clique : cliques) {
            System.out.println("size: " + clique.size());
            if(containsNode(nodeId, clique)) {
                cliquesForNode.add(clique);
            }
        }
        return cliquesForNode;
    }

    /**
     * Check whether a list contains a node id (auxiliary function)
     * @param nodeId Target node id
     * @param list   List to check
     * @return Determines whether the list contains the node
     */
    private boolean containsNode(final Long nodeId, final List<Long> list) {
        for(final Long element : list)
            if (Objects.equals(element, nodeId))
                return true;
        return false;
    }

    /**
     * Initializes clique detection for a given graph.
     * @param graph Graph to check
     */
    private void init(final BaseGraph graph) {
        LOGGER.info("Initializing clique detection ...");
        final List<Long> nodesInitial = new ArrayList<>();
        // add all graph nodes to "remaining" list
        for(final Node node : graph.getNodes()) {
            nodesInitial.add(node.getId());
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

    public List<List<Long>> getCliques() {
        return cliques;
    }

}
