package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
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
     * @param graph       The graph in which the source node resides
     * @param startNodeId Node from which the search is initiated
     */
    public static BFSResult breadthFirstSearch(final BaseGraph graph, final long startNodeId, final GraphMode mode) {

        final Queue<Long> queue = new PriorityQueue<>();
        final List<Long> edgePathIds = new ArrayList<>();

        // add all nodes and mark as unvisited
        final Map<Long, Boolean> visited = new HashMap<>();
        for (final Node node : graph.getNodes()) {
            visited.put(node.getId(), false);
        }

        // mark starting node as visited and enqueue
        visited.put(startNodeId, true);
        queue.add(startNodeId);

        // continue to visit nodes via adjacent paths as long as queue is not empty
        while (!queue.isEmpty()) {
            final long currentNodeId = queue.poll();
            final List<Long> neighbors = GraphProcedureUtils.getNeighbors(graph, currentNodeId, mode);
            for (final Long neighborId : neighbors) {
                // if neighbor has not yet been visited -> visit neighbor and mark it accordingly
                final Boolean isVisited = visited.get(neighborId);
                if (isVisited == null || !isVisited) {

                    // find path that was used to traverse from the current node to its neighbor
                    Edge edge = graph.findEdge(Edge.FROM_ID_FIELD, currentNodeId, Edge.TO_ID_FIELD, neighborId);
                    if (edge == null && mode.equals(GraphMode.UNDIRECTED)) {
                        edge = graph.findEdge(Edge.FROM_ID_FIELD, neighborId, Edge.TO_ID_FIELD, currentNodeId);
                    }
                    if (edge != null) {
                        edgePathIds.add(edge.getId());
                    }

                    visited.put(neighborId, true);
                    queue.add(neighborId);
                }
            }
        }

        // only include visited nodes in result
        final Map<Long, Boolean> filterOnlyVisited = visited.entrySet().stream().filter(Map.Entry::getValue).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new BFSResult(filterOnlyVisited, edgePathIds);
    }

    /**
     * Collects all adjacent neighbors for a node.
     *
     * @param graph  Graph in which the node resides
     * @param nodeId Source node id
     * @param mode   Orientation of the graph, determines which edges are considered
     * @return List with all adjacent neighbors for the node
     */
    public static List<Long> getNeighbors(final BaseGraph graph, final long nodeId, final GraphMode mode) {
        final List<Long> adjacencyList = new ArrayList<>();
        final Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, nodeId);
        for (final Edge edge : outDegrees) {
            adjacencyList.add(edge.getProperty(Edge.TO_ID_FIELD));
        }
        if (mode.equals(GraphMode.UNDIRECTED)) {
            final Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, nodeId);
            for (final Edge edge : inDegrees) {
                adjacencyList.add(edge.getProperty(Edge.FROM_ID_FIELD));
            }
        }
        return adjacencyList;
    }

    /**
     * Creates a subgraph from the open neighborhood of a source node v, i.e. the subgraph of all nodes adjacent to v.
     *
     * @param graph  Graph in which the node resides
     * @param nodeId Source node (not included in result)
     * @param mode   Orientation of the graph, determines which nodes and edges are included
     * @return A subgraph containing the open neighborhood
     */
    public static BaseGraph getOpenNeighborhoodAsSubgraph(final BaseGraph graph, final long nodeId, final GraphMode mode) throws IOException {

        final Graph openNeighborhoodSubgraph = Graph.createTempGraph();

        // gather all outgoing nodes
        final Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, nodeId);
        for (final Edge edge : outDegrees) {
            //openNeighborhoodSubgraph.update(edge);
            openNeighborhoodSubgraph.update(graph.getNode(edge.getProperty(Edge.TO_ID_FIELD)));
        }

        // gather all incoming nodes (undirected graphs only)
        if (mode.equals(GraphMode.UNDIRECTED)) {
            final Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, nodeId);
            for (final Edge edge : inDegrees) {
                //openNeighborhoodSubgraph.update(edge);
                openNeighborhoodSubgraph.update(graph.getNode(edge.getProperty(Edge.FROM_ID_FIELD)));
            }
        }

        for (final Node neighbor : openNeighborhoodSubgraph.getNodes()) {
            final Edge edgeIn = graph.findEdge(Edge.TO_ID_FIELD, neighbor.getId());
            if (edgeIn != null) {
                openNeighborhoodSubgraph.update(edgeIn);
            }
            if (mode.equals(GraphMode.UNDIRECTED)) {
                final Edge edgeOut = graph.findEdge(Edge.FROM_ID_FIELD, neighbor.getId());
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
     * @param graph  Graph in which the node resides
     * @param nodeId Node to be analyzed
     * @param mode   Orientation of the graph
     * @return Largest connected component in the open neighborhood of the node
     */
    public static BFSResult getMaximumConnectedComponent(final BaseGraph graph, final long nodeId,
                                                         final GraphMode mode) throws IOException {

        // calculate open neighborhood (i.e. neighborhood not containing node)
        final BaseGraph openNeighborHood = GraphProcedureUtils.getOpenNeighborhoodAsSubgraph(graph, nodeId, mode);
        final List<BFSResult> neighborHoodComponents = GraphProcedureUtils.findComponentsUndirected(openNeighborHood);

        int size = 0;
        BFSResult largest = null;
        // compare number of nodes in components
        for (final BFSResult component : neighborHoodComponents) {
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
    public static List<BFSResult> findComponentsUndirected(final BaseGraph graph) {

        final List<BFSResult> results = new ArrayList<>();
        final Map<Long, Boolean> nodesVisitedInfo = new HashMap<>();
        final Queue<Long> nodesToVisit = new PriorityQueue<>();

        // enqueue all nodes and mark them as unvisited
        for (final Node node : graph.getNodes()) {
            nodesToVisit.add(node.getId());
            nodesVisitedInfo.put(node.getId(), false);
        }

        while (!nodesToVisit.isEmpty()) {
            final Long currentNodeId = nodesToVisit.poll();
            if (!nodesVisitedInfo.get(currentNodeId)) {
                // do bfs if node has not been visited yet
                BFSResult result = GraphProcedureUtils.breadthFirstSearch(graph, currentNodeId, GraphMode.UNDIRECTED);
                results.add(result);
                // mark all nodes that were visited in course of this search
                for (final long id : result.getVisitedNodes().keySet()) {
                    nodesVisitedInfo.put(id, true);
                }
            }
        }
        return results;
    }
}
