package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.*;

/**
 * Finds shortest paths in a graph
 *
 * TODO:
 *  - implement settings so they don't need to be supplied to every method (mode, weighted (y/n), initialization value, ...)
 *  - create result containers instead of hash maps (distance mappings, hop nodes, ...)
 */
public class ShortestPathFinder {

    private Graph graph;
    private HashMap<Long, Long> shortestPaths;

    public ShortestPathFinder(Graph graph) {
        this.graph = graph;
        this.shortestPaths = new HashMap<>();
    }

    /**
     * Computes the shortest path from a given source to a given target using Dijkstra's algorithm.
     * @param graph The graph containing source and target node
     * @param source The source node
     * @param target The target node
     * @param mode Direction of the graph (directed or undirected)
     * @return Maps the path length from source to target
     */
    public HashMap<Long, Long> dijkstra(final Graph graph, final Node source, final Node target, final GraphMode mode) {

        HashMap<Long, Long> distances = new HashMap<>();
        for (Node node : graph.getNodes()) {
            if (node.getId() != source.getId()) {
                distances.put(node.getId(), Long.MAX_VALUE);
            }
        }
        distances.put(source.getId(), Long.valueOf(0));
        PriorityQueue<DistancePair> queue = new PriorityQueue<>();
        queue.add(new DistancePair(source, 0));

        while(!queue.isEmpty()) {

            // get next node with smallest distance
            DistancePair pair = queue.poll();
            Node current = pair.getNode();
            long distanceCurrent = pair.getDistance();

            // Target was reached, stop computation
            if(current.getId().equals(target.getId())) { break; }

            ArrayList<Node> neighbors = GraphProcedureUtils.getNeighbors(graph, current, mode);
            for (Node neighbor : neighbors) {
                long neighborID = neighbor.getId();
                if (distances.get(neighborID) > (distanceCurrent + 1)) {
                    distances.put(neighborID, distanceCurrent + 1);
                    queue.add(new DistancePair(neighbor, distances.get(neighborID)));
                }
            }
        }
        // filter result map so it only contains the source-target-pair
        distances.keySet().removeIf(key -> !key.equals(target.getId()));
        return distances;
    }

    /**
     * Finds the lengths of all shortest paths from a source node to all other nodes in a graph using Dijkstra's
     * algorithm.
     * @param graph      Graph model
     * @param sourceNode Starting node
     * @param mode       Orientation of the graph
     * @param setSelfInfinity Determines whether the distance from a node to itself will be set to ∞
     *                            (Please note that this is not the "actual infinity value", but the max value for the Long instance!)
     * @param labels     Filters the algorithm's output by specific node labels
     * @return A mapping showing all nodes and their shortest paths from source node
     */
    public static HashMap<Long, Long> dijkstra(final Graph graph, final Node sourceNode, final GraphMode mode, final boolean setSelfInfinity,
                                               final String... labels) {

        // init distance mapping and node queue
        HashMap<Long, Long> distances = new HashMap<>();
        for (Node node : graph.getNodes()) {
            if (node.getId() != sourceNode.getId()) {
                distances.put(node.getId(), Long.MAX_VALUE);
            }
        }
        distances.put(sourceNode.getId(), Long.valueOf(0));
        PriorityQueue<DistancePair> queue = new PriorityQueue<>();
        queue.add(new DistancePair(sourceNode, 0));

        while (!queue.isEmpty()) {

            // remove head of queue and set as current node
            DistancePair current = queue.poll();
            Node currentNode = current.getNode();

            long currentDistance = current.getDistance();
            ArrayList<Node> neighbors = GraphProcedureUtils.getNeighbors(graph, currentNode, mode);

            // For each adjacent neighbor: Update distance if required
            for (Node neighbor : neighbors) {
                long neighborID = neighbor.getId();
                if (distances.get(neighborID) > (currentDistance + 1)) {
                    distances.put(neighborID, currentDistance + 1);
                    queue.add(new DistancePair(neighbor, distances.get(neighborID)));
                }
            }
        }

        // Set distance from node to itself to "infinity" if desired
        if(setSelfInfinity) {
            distances.put(sourceNode.getId(), Long.MAX_VALUE);
        }

        List<String> labelsList = Arrays.asList(labels);
        if(!labelsList.isEmpty()) {
            distances.entrySet().removeIf(entry -> !labelsList.contains(graph.getNode(entry.getKey()).getLabel()));
        }
        return distances;
    }

}
