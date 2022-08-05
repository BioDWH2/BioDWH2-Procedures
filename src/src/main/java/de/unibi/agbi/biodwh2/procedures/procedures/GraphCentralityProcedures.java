package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.utils.BFSResult;
import de.unibi.agbi.biodwh2.procedures.utils.GraphCliqueFinder;
import de.unibi.agbi.biodwh2.procedures.utils.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.GraphProcedureUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contains database procedures for centrality measures and analysis.
 */
public final class GraphCentralityProcedures implements RegistryContainer {

    /**
     * Calculates the degree of a node, i.e. the number of adjacent neighbors.
     * @param graph Graph object
     * @param nodeID ID of the source node
     * @return Result set showing the node's ID and its degree
     */
    @Procedure(name = "analysis.network.centrality.degree", signature = "TODO", description = "Calculates the degree of a graph node")
    public static ResultSet degree(final Graph graph, final long nodeID) {
        long degree = 0;
        Iterable<Edge> outDegrees = graph.findEdges(Edge.FROM_ID_FIELD, nodeID);
        Iterable<Edge> inDegrees = graph.findEdges(Edge.TO_ID_FIELD, nodeID);
        degree = Stream.concat(StreamSupport.stream(outDegrees.spliterator(), false), StreamSupport.stream(inDegrees.spliterator(), false)).count();
        final ResultSet result = new ResultSet("id", "degree");
        result.addRow(new ResultRow(new String[]{"id", "degree"}, new Object[]{nodeID, degree}));
        return result;
    }

    /**
     * Calculates the closeness of a node, i.e. the reciprocal of the sum of all shortest paths between the node
     * in question and all other nodes in the graph.
     * @param graph The graph in which the node resides
     * @param node Source node
     * @param mode Orientation of the graph
     * @return Result set showing the node's ID and its closeness value
     */
    @Procedure(name = "analysis.network.centrality.closeness", signature = "TODO", description = "Calculates the closeness of a graph node")
    public static ResultSet closeness(final Graph graph, final Node node, final GraphMode mode) {
        double closeness = 0;
        HashMap<Long, Long> distances = GraphProcedureUtils.dijkstra(graph, node, mode, false);
        distances.remove(node.getId());
        for(long distance : distances.values()) {
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
    @Procedure(name = "analysis.network.centrality.eccentricity", signature = "TODO", description = "Calculates the eccentricity of a node")
    public static ResultSet eccentricity(final Graph graph, final Node node, final GraphMode mode) {
        HashMap<Long, Long> distances = GraphProcedureUtils.dijkstra(graph, node, mode, false);
        double eccentricity = (1.0 / Collections.max(distances.values()));
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "eccentricity"}, new Object[]{node.getId(), eccentricity}));
        return result;
    }

    /**
     * Calculates the maximum neighborhood component of a node, i.e. the size of the maximum connected component
     * of the source node's neighborhood.
     * @param graph Graph in which the node resides
     * @param node Source node
     * @param mode Orientation of the graph
     * @return Result row containing the size of the maximum connected component
     * @throws IOException
     */
    @Procedure(name = "analysis.network.centrality.mnc", signature = "TODO", description = "Calculates the maximum neighborhood component for a node")
    public static ResultSet maximumNeighborhoodComponent(final Graph graph, final Node node, final GraphMode mode) throws IOException {
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph, node, mode);
        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "mnc"}, new Object[]{node.getId(), maximumConnectedComponent.getVisitedNodes().size()}));
        return result;
    }

    /**
     * Calculates the density of the maximum neighborhood component for a given node.
     * @param graph The graph in which the node resides
     * @param node The node to be analyzed
     * @param mode Orientation of the graph
     * @param epsilon
     * @return Result containing the node's id and the corresponding density of maximum neighborhood component
     * @throws IOException
     */
    @Procedure(name = "analysis.network.centrality.dmnc", signature = "TODO", description = "Calculates the density of the maximum neighborhood component for a node")
    public static ResultSet densityOfMaximumNeighborhoodComponent(final Graph graph, final Node node, final GraphMode mode, final double epsilon) throws IOException {
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph, node, mode);
        double density = maximumConnectedComponent.getPaths().size() / Math.pow(maximumConnectedComponent.getVisitedNodes().size(), epsilon);
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
    @Procedure(name = "analysis.network.centrality.mcc", signature = "TODO", description = "Calculates the maximal clique centrality of a given node")
    public static ResultSet maximalCliqueCentrality(final Graph graph, final Node node) {
        // find cliques in graph
        GraphCliqueFinder graphCliqueFinder = new GraphCliqueFinder(graph);
        ArrayList<Node> nodes = new ArrayList<>();
        graph.getNodes().forEach(n -> nodes.add(n));
        graphCliqueFinder.findCliques(graph, new ArrayList<>(), nodes, new ArrayList<>(), 0);

        // obtain all cliques containing target node
        ArrayList<ArrayList<Node>> cliquesForNode = graphCliqueFinder.getCliquesForNode(node.getId());

        // calculate score
        int mcc = 0;
        for(ArrayList<Node> clique : cliquesForNode) {
            mcc += factorial(clique.size() - 1);
        }

        ResultSet result = new ResultSet();
        result.addRow(new ResultRow(new String[]{"id", "mcc"}, new Object[]{node.getId(), mcc}));
        return result;
    }

    /**
    * TODO: move this somewhere else ...
     */
    private static int factorial(int number) {
        if(number == 0) {
            return 1;
        } else {
            return number * factorial(number - 1);
        }
    }

}



























