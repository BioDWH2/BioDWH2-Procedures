package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import de.unibi.agbi.biodwh2.procedures.model.IdPair;
import de.unibi.agbi.biodwh2.procedures.utils.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contains database procedures for centrality measures and analysis.
 */
public final class GraphCentralityProcedures implements RegistryContainer {

    /**
     * Calculates the degree of a node, i.e. all outgoing and incoming edges.
     * @param graph Graph object
     * @param node Source node
     * @return Result set showing the node's ID and its degree
     */
    @Procedure(name = "analysis.network.centrality.degree", description = "Calculates the degree of a graph node")
    public static ResultSet degree(final BaseGraph graph, final Node node) {
        long degree = 0;
        long id = node.getId();
        Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, id);
        Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, id);
        degree = Stream.concat(StreamSupport.stream(outDegrees.spliterator(), false), StreamSupport.stream(inDegrees.spliterator(), false)).count();
        final ResultSet result = new ResultSet("id", "degree");
        result.addRow(new ResultRow(new String[]{"id", "degree"}, new Object[]{id, degree}));
        return result;
    }

    /**
     * Calculates the in degree, i.e. all outgoing edges from a node
     * @param graph Graph object
     * @param node Source node
     * @return Result set containing the in degree and the node's id
     */
    @Procedure(name = "analysis.network.centrality.degree.in", description = "Calculates the in degree for a node, i.e. all outgoing edges")
    public static  ResultSet degreeIn(final BaseGraph graph, final Node node) {
        long id = node.getId();
        long degreeIn = StreamSupport.stream(graph.findEdges(Edge.TO_ID_FIELD, id).spliterator(), false).count();
        final ResultSet result = new ResultSet("id", "in degree");
        result.addRow(new ResultRow(new String[]{"id", "degree"}, new Object[]{id, degreeIn}));
        return result;
    }

    /**
     * Calculates the out degree for a node, i.e. all incoming edges.
     * @param graph Graph object
     * @param node Source node
     * @return Result set containing the out degree and the node's id
     */
    @Procedure(name = "analysis.network.centrality.degree.out", description = "Calculates the out degree for a node, i.e. all incoming edges")
    public static  ResultSet degreeOut(final BaseGraph graph, final Node node) {
        long id = node.getId();
        long degreeOut = StreamSupport.stream(graph.findEdges(Edge.FROM_ID_FIELD, id).spliterator(), false).count();
        final ResultSet result = new ResultSet("id", "out degree");
        result.addRow(new ResultRow(new String[]{"id", "degree"}, new Object[]{id, degreeOut}));
        return result;
    }

    /**
     * Calculates the closeness of a node, i.e. the reciprocal of the sum of all shortest paths between the node
     * in question and all other nodes in the graph.
     * @param graph The graph in which the node resides
     * @param node Source node
     * @param mode Orientation of the graph
     * @param labels Restricts the closeness calculation to nodes with certain labels (optional parameter)
     * @return Result set showing the node's ID and its closeness value
     */
    @Procedure(name = "analysis.network.centrality.closeness", description = "Calculates the closeness of a graph node")
    public static ResultSet closeness(final BaseGraph graph, final Node node, final GraphMode mode, final String... labels) {
        double closeness = 0;
        final ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, mode);
        final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(node.getId(), false, labels);
        dijkstraResult.getDistances().remove(node.getId());
        for(long distance : dijkstraResult.getDistances().values()) {
            closeness += distance;
        }
        closeness =  (graph.getNumberOfNodes() - 1) / closeness;
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "closeness"}, new Object[]{node.getId(), closeness}));
        return result;
    }

    /**
     * Calculates the eccentricity of a node by estimating the longest shortest path from a node to all other nodes in the graph.
     * @param graph The graph in which the node resides
     * @param node Source node
     * @param mode Orientation of the graph
     * @return Result set containing the source node's id and its eccentricity value
     */
    @Procedure(name = "analysis.network.centrality.eccentricity", description = "Calculates the eccentricity of a node")
    public static ResultSet eccentricity(final BaseGraph graph, final Node node, final GraphMode mode) {
        final ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, mode);
        final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(node.getId(), false);
        double eccentricity = (1.0 / Collections.max(dijkstraResult.getDistances().values()));
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "eccentricity"}, new Object[]{node.getId(), eccentricity}));
        return result;
    }

    /**
     * Calculates betweenness centrality for a given node. For each node pair in the graph, the ratio between the total number of
     * shortest paths and the number of shortest paths that have the target node on them is computed. All ratios are then summed
     * up, forming the betweenness score for a target node. To prevent node pairs from being processed twice, all processed pairs are
     * stored in a list for a lookup before the shortest paths are computed.
     * @param graph The graph in which the node resides
     * @param nodeId ID of the target node
     * @param mode Orientation of the graph
     * @return Result set containing the source node's id and its betweenness value
     */
    @Procedure(name = "analysis.network.centrality.betweenness", description="Calculates betweenness centrality for a given node")
    public static ResultSet betweenness(final BaseGraph graph, final long nodeId, final GraphMode mode) {
        final ShortestPathFinder shortestPathFinder = new ShortestPathFinder(graph, mode);
        final ArrayList<IdPair> processedPairs = new ArrayList<>();
        double betweenness = 0;
        for(Node node : graph.getNodes()) {
            long nodeFirstId = node.getId();
            for(Node otherNode : graph.getNodes()) {
                long nodeSecondId = otherNode.getId();
                IdPair currentPair = new IdPair(nodeFirstId, nodeSecondId);
                if(!processedPairs.contains(currentPair)) {
                    // calculate ratio if node pair has not been processed yet
                    if(!Objects.equals(nodeFirstId, nodeSecondId) && !Objects.equals(nodeFirstId, nodeId) && !Objects.equals(nodeSecondId, nodeId)) {
                        ArrayList<ArrayList<Long>> allShortestPaths = shortestPathFinder.findAllShortestPaths(nodeFirstId, nodeSecondId);
                        if(allShortestPaths.size() > 0) {
                            // sum up ratio between number of shortest paths between the two nodes and the number of shortest paths passing through the target
                            betweenness += (1.0 * ShortestPathFinder.countPathsWithNodeAsWaypoint(allShortestPaths, nodeId) / allShortestPaths.size());
                        }
                    }
                    // pair has been processed -> add to list
                    processedPairs.add(currentPair);
                }
            }
        }
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "betweenness"}, new Object[]{nodeId, betweenness}));
        return result;
    }

    /**
     * Calculates the maximum neighborhood component of a node, i.e. the size of the maximum connected component
     * of the source node's neighborhood.
     * @param graph Graph in which the node resides
     * @param node Source node
     * @param mode Orientation of the graph
     * @return Result row containing the size of the maximum connected component
     */
    @Procedure(name = "analysis.network.centrality.mnc", description = "Calculates the maximum neighborhood component for a node")
    public static ResultSet maximumNeighborhoodComponent(final BaseGraph graph, final Node node, final GraphMode mode) throws IOException {
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph, node.getId(), mode);
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "mnc"}, new Object[]{node.getId(), maximumConnectedComponent.getNodeIds().size()}));
        return result;
    }

    /**
     * Calculates the density of the maximum neighborhood component for a given node.
     * @param graph The graph in which the node resides
     * @param node The node to be analyzed
     * @param mode Orientation of the graph
     * @param epsilon
     * @return Result containing the node's id and the corresponding density of maximum neighborhood component
     */
    @Procedure(name = "analysis.network.centrality.dmnc", description = "Calculates the density of the maximum neighborhood component for a node")
    public static ResultSet densityOfMaximumNeighborhoodComponent(final BaseGraph graph, final Node node, final GraphMode mode, final double epsilon) throws IOException {
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph, node.getId(), mode);
        double density = maximumConnectedComponent.getEdgePathIds().size() / Math.pow(maximumConnectedComponent.getNodeIds().size(), epsilon);
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "dmnc"}, new Object[]{node.getId(), density}));
        return result;
    }

    /**
     * Calculates the maximal clique centrality for a given node.
     * @param graph Graph in which the node resides
     * @param node Target node
     * @return Result set containing the node's and its MCC score
     */
    @Procedure(name = "analysis.network.centrality.mcc", description = "Calculates the maximal clique centrality of a given node")
    public static ResultSet maximalCliqueCentrality(final BaseGraph graph, final Node node) {
        // find cliques in graph
        GraphCliqueFinder graphCliqueFinder = new GraphCliqueFinder(graph);
        //ArrayList<Node> nodes = new ArrayList<>();
        //graph.getNodes().forEach(n -> nodes.add(n));

        // obtain all cliques containing target node
        final List<List<Long>> cliquesForNode = graphCliqueFinder.getCliquesForNodeId(node.getId());
        System.out.println("cliques containing node: " + cliquesForNode.size());

        // calculate score
        int mcc = 0;
        for(final List<Long> clique : cliquesForNode) {
            mcc += MathUtils.factorial(clique.size() - 1);
        }

        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "mcc"}, new Object[]{node.getId(), mcc}));
        return result;
    }

}
