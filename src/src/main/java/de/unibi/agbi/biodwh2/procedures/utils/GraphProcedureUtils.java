package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
import de.unibi.agbi.biodwh2.procedures.model.DistancePair;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for all functions supporting graph procedures.
 */
public class GraphProcedureUtils {

    /**
     * Performs a breadth-first search in the course of which all connected nodes starting from a single source are
     * marked as visited.
     * <p>
     * TODO: use nodes instead of their ids for queue and implement comparable in node
     *
     * @param graph     The graph in which the source node resides
     * @param nodeStart Node from which the search is initiated
     */
    public static BFSResult breadthFirstSearch(final Graph graph, Node nodeStart, final GraphMode mode) {

        PriorityQueue<Long> queue = new PriorityQueue<>();
        ArrayList<Edge> paths = new ArrayList<>();

        // add all nodes and mark as unvisited
        HashMap<Long, Boolean> visited = new HashMap<>();
        for (Node node : graph.getNodes()) {
            visited.put(node.getId(), false);
        }

        // mark starting node as visited and enqueue
        visited.put(nodeStart.getId(), true);
        queue.add(nodeStart.getId());

        // continue to visit nodes via adjacent paths as long as queue is not empty
        while (!queue.isEmpty()) {
            Node currentNode = graph.getNode(queue.poll());
            ArrayList<Node> neighbors = GraphProcedureUtils.getNeighbors(graph, currentNode, mode);
            for (Node neighbor : neighbors) {
                // if neighbor has not yet been visited -> visit neighbor and mark it accordingly
                long id = neighbor.getId();
                if (!visited.get(id)) {

                    // find path that was used to traverse from the current node to its neighbor
                    Edge edge = graph.findEdge(Edge.FROM_ID_FIELD, currentNode.getId(), Edge.TO_ID_FIELD,
                                               neighbor.getId());
                    if (edge == null && mode.equals(GraphMode.UNDIRECTED)) {
                        edge = graph.findEdge(Edge.FROM_ID_FIELD, neighbor.getId(), Edge.TO_ID_FIELD,
                                              currentNode.getId());
                    }
                    if (edge != null) {
                        paths.add(edge);
                    }

                    visited.put(id, true);
                    queue.add(id);
                }
            }
        }

        // only include visited nodes in result
        Map filterOnlyVisited = visited.entrySet().stream().filter(isVisited -> isVisited.getValue()).collect(
                Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        return new BFSResult((HashMap<Long, Boolean>) filterOnlyVisited, paths);
    }

    /**
     * Collects all adjacent neighbors for a node.
     *
     * @param graph Graph in which the node resides
     * @param node  Source node
     * @param mode  Orientation of the graph, determines which edges are considered
     * @return List with all adjacent neighbors for the node
     */
    public static ArrayList<Node> getNeighbors(Graph graph, Node node, GraphMode mode) {
        ArrayList<Node> adjacencyList = new ArrayList<>();
        Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, node.getId());
        for (Edge edge : outDegrees) {
            Node outDegreeNeighbor = graph.getNode(edge.getProperty(Edge.TO_ID_FIELD));
            if (outDegreeNeighbor != null) {
                adjacencyList.add(outDegreeNeighbor);
            }
        }
        if (mode.equals(GraphMode.UNDIRECTED)) {
            Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, node.getId());
            for (Edge edge : inDegrees) {
                Node inDegreeNeighbor = graph.getNode(edge.getProperty(Edge.FROM_ID_FIELD));
                if (inDegreeNeighbor != null) {
                    adjacencyList.add(inDegreeNeighbor);
                }
            }
        }
        return adjacencyList;
    }

    /**
     * Creates a subgraph from the open neighborhood of a source node v, i.e. the subgraph of all nodes adjacent to v.
     *
     * @param graph Graph in which the node resides
     * @param node  Source node (not included in result)
     * @param mode  Orientation of the graph, determines which nodes and edges are included
     * @return A subgraph containing the open neighborhood
     */
    public static Graph getOpenNeighborhoodAsSubgraph(Graph graph, Node node, final GraphMode mode) throws IOException {

        Graph openNeighborhoodSubgraph = Graph.createTempGraph();

        // gather all outgoing nodes
        Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, node.getId());
        for (Edge edge : outDegrees) {
            //openNeighborhoodSubgraph.update(edge);
            openNeighborhoodSubgraph.update(graph.getNode(edge.getProperty(Edge.TO_ID_FIELD)));
        }

        // gather all incoming nodes (undirected graphs only)
        if (mode.equals(GraphMode.UNDIRECTED)) {
            Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, node.getId());
            for (Edge edge : inDegrees) {
                //openNeighborhoodSubgraph.update(edge);
                openNeighborhoodSubgraph.update(graph.getNode(edge.getProperty(Edge.FROM_ID_FIELD)));
            }
        }

        for (Node neighbor : openNeighborhoodSubgraph.getNodes()) {
            Edge edgeIn = graph.findEdge(Edge.TO_ID_FIELD, neighbor.getId());
            if (edgeIn != null) {
                openNeighborhoodSubgraph.update(edgeIn);
            }
            if (mode.equals(GraphMode.UNDIRECTED)) {
                Edge edgeOut = graph.findEdge(Edge.FROM_ID_FIELD, neighbor.getId());
                if (edgeOut != null) {
                    openNeighborhoodSubgraph.update(edgeOut);
                }
            }

        }
        return openNeighborhoodSubgraph;
    }

    /**
     * Finds the largest connected component in a graph inside from the neighborhood of a specified node.
     *
     * @param graph Graph in which the node resides
     * @param node  Node to be analyzed
     * @param mode  Orientation of the graph
     * @return Largest connected component in the open neighborhood of the node
     */
    public static BFSResult getMaximumConnectedComponent(final Graph graph, final Node node,
                                                         final GraphMode mode) throws IOException {

        // calculate open neighborhood (i.e. neighborhood not containing node)
        Graph openNeighborHood = GraphProcedureUtils.getOpenNeighborhoodAsSubgraph(graph, node, mode);
        ArrayList<BFSResult> neighborHoodComponents = GraphProcedureUtils.findComponentsUndirected(openNeighborHood);

        int size = 0;
        BFSResult largest = null;
        // compare number of nodes in components
        for (BFSResult component : neighborHoodComponents) {
            int componentSize = component.getVisitedNodes().size();
            if (componentSize > size) {
                size = componentSize;
                largest = component;
            }
        }
        return largest;
    }

    /**
     * Extracts all connected components from a graph using the breadth-first search (BFS). Note that each search
     * reveals exactly one connected component and no component occurs twice.
     *
     * @param graph Graph that is supposed to be divided into components
     * @return List of all BFS results containing the node IDs and edges of the components
     */
    public static ArrayList<BFSResult> findComponentsUndirected(final Graph graph) {

        ArrayList<BFSResult> results = new ArrayList<>();
        HashMap<Long, Boolean> nodesVisitedInfo = new HashMap<>();
        PriorityQueue<Long> nodesToVisit = new PriorityQueue<>();

        // enqueue all nodes and mark them as unvisited
        for (Node node : graph.getNodes()) {
            nodesToVisit.add(node.getId());
            nodesVisitedInfo.put(node.getId(), false);
        }

        while (!nodesToVisit.isEmpty()) {
            Long currentNodeID = nodesToVisit.poll();
            if (!nodesVisitedInfo.get(currentNodeID)) {
                // do bfs if node has not been visited yet
                BFSResult result = GraphProcedureUtils.breadthFirstSearch(graph, graph.getNode(currentNodeID),
                                                                          GraphMode.UNDIRECTED);
                results.add(result);
                // mark all nodes that were visited in course of this search
                for (long id : result.getVisitedNodes().keySet()) {
                    nodesVisitedInfo.put(id, true);
                }
            }
        }
        return results;
    }
}
