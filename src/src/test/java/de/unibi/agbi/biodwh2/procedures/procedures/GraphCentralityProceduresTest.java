package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphCentralityProceduresTest {

    private Graph graphDisconnected;
    private Graph graphConnected;

    @BeforeAll
    void setup() throws IOException {

        graphDisconnected = Graph.createTempGraph();
        final Node nodeA = graphDisconnected.addNode("A");
        final Node nodeB = graphDisconnected.addNode("B");
        final Node nodeC = graphDisconnected.addNode("C");
        final Node nodeD = graphDisconnected.addNode("D");
        final Node nodeE = graphDisconnected.addNode("E");
        final Node nodeF = graphDisconnected.addNode("F");
        final Node nodeG = graphDisconnected.addNode("G");
        graphDisconnected.addEdge(nodeA, nodeB, "eAB");
        graphDisconnected.addEdge(nodeB, nodeD, "eBD");
        graphDisconnected.addEdge(nodeC, nodeB, "eCB");
        graphDisconnected.addEdge(nodeC, nodeE, "eCE");
        graphDisconnected.addEdge(nodeD, nodeF, "eDF");
        graphDisconnected.addEdge(nodeE, nodeD, "eED");
        graphDisconnected.addEdge(nodeE, nodeF, "eEF");
        graphDisconnected.addEdge(nodeE, nodeE, "eEE");

        graphConnected = Graph.createTempGraph();
        final Node nodeA2 = graphConnected.addNode("A");
        final Node nodeB2 = graphConnected.addNode("B");
        final Node nodeC2 = graphConnected.addNode("C");
        final Node nodeD2 = graphConnected.addNode("D");
        final Node nodeE2 = graphConnected.addNode("E");
        final Node nodeF2 = graphConnected.addNode("F");
        final Node nodeG2 = graphConnected.addNode("G");
        graphConnected.addEdge(nodeA2, nodeC2, "eAC");
        graphConnected.addEdge(nodeA2, nodeB2, "eAB");
        graphConnected.addEdge(nodeA2, nodeD2, "eAD");
        graphConnected.addEdge(nodeC2, nodeF2, "eCF");
        graphConnected.addEdge(nodeF2, nodeD2, "eFD");
        graphConnected.addEdge(nodeD2, nodeE2, "eDE");
        graphConnected.addEdge(nodeF2, nodeG2, "eFG");
        graphConnected.addEdge(nodeB2, nodeE2, "eBE");
        graphConnected.addEdge(nodeE2, nodeG2, "eEG");
    }

    @Test
    void degreeTest() {
        assertEquals(5, (long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("E"))
                                                        .getRow(0).getValue(1));
        assertEquals(1, (long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("A"))
                                                        .getRow(0).getValue(1));
        assertEquals(0, (long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("G"))
                                                        .getRow(0).getValue(1));
    }

    @Test
    void degreeInTest() {
        assertEquals(2, (long) GraphCentralityProcedures.degreeIn(graphDisconnected, graphDisconnected.findNode("B"))
                                                        .getRow(0).getValue(1));
        assertEquals(2, (long) GraphCentralityProcedures.degreeIn(graphDisconnected, graphDisconnected.findNode("E"))
                                                        .getRow(0).getValue(1));
        assertEquals(0, (long) GraphCentralityProcedures.degreeIn(graphDisconnected, graphDisconnected.findNode("G"))
                                                        .getRow(0).getValue(1));
    }

    @Test
    void degreeOutTest() {
        assertEquals(1, (long) GraphCentralityProcedures.degreeOut(graphDisconnected, graphDisconnected.findNode("B"))
                                                        .getRow(0).getValue(1));
        assertEquals(3, (long) GraphCentralityProcedures.degreeOut(graphDisconnected, graphDisconnected.findNode("E"))
                                                        .getRow(0).getValue(1));
        assertEquals(0, (long) GraphCentralityProcedures.degreeOut(graphDisconnected, graphDisconnected.findNode("G"))
                                                        .getRow(0).getValue(1));
    }

    @Test
    void closenessTest() throws IOException {
        final Graph graph = Graph.createTempGraph();
        final Node nodeA = graph.addNode("A");
        final Node nodeB = graph.addNode("B");
        final Node nodeC = graph.addNode("C");
        final Node nodeD = graph.addNode("D");
        final Node nodeE = graph.addNode("E");
        final Node nodeF = graph.addNode("F");
        final Node nodeG = graph.addNode("G");
        final Node nodeH = graph.addNode("H");
        graph.addEdge(nodeA, nodeB, "eAB");
        graph.addEdge(nodeB, nodeC, "eBC");
        graph.addEdge(nodeC, nodeD, "eAB");
        graph.addEdge(nodeD, nodeE, "eDE");
        graph.addEdge(nodeD, nodeH, "eDH");
        graph.addEdge(nodeE, nodeF, "eEF");
        graph.addEdge(nodeE, nodeG, "eEG");
        graph.addEdge(nodeE, nodeH, "eEH");
        graph.addEdge(nodeF, nodeG, "eFG");
        graph.addEdge(nodeH, nodeG, "eHG");

        double degD = (double) GraphCentralityProcedures.closeness(graph, graph.findNode("D"), GraphMode.UNDIRECTED)
                                                        .getRow(0).getValue(1);
        double degA = (double) GraphCentralityProcedures.closeness(graph, graph.findNode("A"), GraphMode.UNDIRECTED)
                                                        .getRow(0).getValue(1);
        assertTrue(degD > degA);
    }

    @Test
    void eccentricityTest() {
        assertEquals(1.0 / 3, (double) GraphCentralityProcedures.eccentricity(graphConnected,
                                                                              graphConnected.findNode("A"),
                                                                              GraphMode.UNDIRECTED).getRow(0)
                                                                .getValue(1));
        assertEquals(1.0 / 3, (double) GraphCentralityProcedures.eccentricity(graphConnected,
                                                                              graphConnected.findNode("B"),
                                                                              GraphMode.UNDIRECTED).getRow(0)
                                                                .getValue(1));
        assertEquals(1.0 / 2, (double) GraphCentralityProcedures.eccentricity(graphConnected,
                                                                              graphConnected.findNode("D"),
                                                                              GraphMode.UNDIRECTED).getRow(0)
                                                                .getValue(1));
    }

    @Test
    void betweennessTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node node1 = graph.addNode("1");
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        Node node6 = graph.addNode("6");
        Node node7 = graph.addNode("7");
        Node node8 = graph.addNode("8");
        Node node9 = graph.addNode("9");
        graph.addEdge(node1, node2, "e1-2");
        graph.addEdge(node1, node3, "e1-3");
        graph.addEdge(node1, node4, "e1-4");
        graph.addEdge(node2, node3, "e2-3");
        graph.addEdge(node3, node4, "e3-4");
        graph.addEdge(node4, node5, "e4-5");
        graph.addEdge(node4, node6, "e4-6");
        graph.addEdge(node5, node6, "e5-6");
        graph.addEdge(node5, node7, "e5-7");
        graph.addEdge(node5, node8, "e5-8");
        graph.addEdge(node6, node7, "e6-7");
        graph.addEdge(node6, node8, "e6-8");
        graph.addEdge(node7, node8, "e7-8");
        graph.addEdge(node7, node9, "e7-9");

        long numNodes = graph.getNumberOfNodes();
        double normalizationUndirected = ((numNodes - 1) * (numNodes - 2)) / 2;
        double expected7 = 7.0;
        double expected7Normalized = expected7 / normalizationUndirected;
        double expected4 = 15.0;
        double expected4Normalized = expected4 / normalizationUndirected;

        assertEquals(expected7, GraphCentralityProcedures.betweenness(graph, node7, GraphMode.UNDIRECTED, false).getRow(0).getValue("betweenness"));
        assertEquals(expected4, GraphCentralityProcedures.betweenness(graph, node4, GraphMode.UNDIRECTED, false).getRow(0).getValue("betweenness"));
        assertEquals(expected7Normalized, GraphCentralityProcedures.betweenness(graph, node7, GraphMode.UNDIRECTED, true).getRow(0).getValue("betweenness"));
        assertEquals(expected4Normalized, GraphCentralityProcedures.betweenness(graph, node4, GraphMode.UNDIRECTED, true).getRow(0).getValue("betweenness"));
    }

    @Test
    void betweennessApproximatedTest() throws IOException {

        Graph graph = Graph.createTempGraph();
        Node node1 = graph.addNode("1");
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        Node node6 = graph.addNode("6");
        Node node7 = graph.addNode("7");
        Node node8 = graph.addNode("8");
        Node node9 = graph.addNode("9");
        graph.addEdge(node1, node2, "e1-2");
        graph.addEdge(node1, node3, "e1-3");
        graph.addEdge(node1, node4, "e1-4");
        graph.addEdge(node2, node3, "e2-3");
        graph.addEdge(node3, node4, "e3-4");
        graph.addEdge(node4, node5, "e4-5");
        graph.addEdge(node4, node6, "e4-6");
        graph.addEdge(node5, node6, "e5-6");
        graph.addEdge(node5, node7, "e5-7");
        graph.addEdge(node5, node8, "e5-8");
        graph.addEdge(node6, node7, "e6-7");
        graph.addEdge(node6, node8, "e6-8");
        graph.addEdge(node7, node8, "e7-8");
        graph.addEdge(node7, node9, "e7-9");

        ResultSet result = GraphCentralityProcedures.betweennessApproximated(graph, 9, GraphMode.DIRECTED);
        HashMap<Long, Float> betweenessCentralities = (HashMap<Long, Float>) result.getRow(0).getValue("betweenness centralities");

        for (Map.Entry<Long, Float> entry : betweenessCentralities.entrySet()) {
            String label = graph.getNodeLabel(entry.getKey());
            System.out.println("B(" + label + "): " + entry.getValue());
        }

    }


    @Test
    void maximumNeighborhoodComponentTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node node1 = graph.addNode("1");
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        Node node6 = graph.addNode("6");
        graph.addEdge(node1, node2, "e1-2");
        graph.addEdge(node1, node5, "e1-5");
        graph.addEdge(node2, node3, "e2-3");
        graph.addEdge(node2, node5, "e2-5");
        graph.addEdge(node3, node4, "e3-4");
        graph.addEdge(node4, node5, "e4-5");
        graph.addEdge(node4, node6, "e4-6");
        assertEquals(2, (int) GraphCentralityProcedures.maximumNeighborhoodComponent(graph, graph.findNode("5"),
                                                                                     GraphMode.UNDIRECTED).getRow(0)
                                                       .getValue(1));
    }

    @Test
    void densityOfMaximumNeighborhoodComponentTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node node1 = graph.addNode("1");
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        Node node6 = graph.addNode("6");
        graph.addEdge(node1, node2, "e1-2");
        graph.addEdge(node1, node5, "e1-5");
        graph.addEdge(node2, node3, "e2-3");
        graph.addEdge(node2, node5, "e2-5");
        graph.addEdge(node3, node4, "e3-4");
        graph.addEdge(node4, node5, "e4-5");
        graph.addEdge(node4, node6, "e4-6");
        final Node sourceNode = graph.findNode("5");
        final ResultSet result = GraphCentralityProcedures.densityOfMaximumNeighborhoodComponent(graph, sourceNode,
                                                                                                 GraphMode.UNDIRECTED,
                                                                                                 1.7);
        assertEquals(0.3077861033362291, result.getRow(0).getValue(1));
    }

    @Test
    void maximalCliqueCentralityTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node nodeA = graph.addNode("A");
        Node nodeB = graph.addNode("B");
        Node nodeC = graph.addNode("C");
        Node nodeD = graph.addNode("D");
        Node nodeE = graph.addNode("E");
        Node nodeF = graph.addNode("F");

        graph.addEdge(nodeA, nodeB, "eAB");
        graph.addEdge(nodeA, nodeC, "eAC");
        graph.addEdge(nodeA, nodeE, "eAE");
        graph.addEdge(nodeB, nodeC, "eBC");
        graph.addEdge(nodeB, nodeD, "eBD");
        graph.addEdge(nodeB, nodeF, "eBF");
        graph.addEdge(nodeC, nodeD, "eCD");
        graph.addEdge(nodeC, nodeF, "eCF");
        graph.addEdge(nodeD, nodeF, "eDF");
        graph.addEdge(nodeD, nodeE, "eDE");

        assertEquals(3, (int) GraphCentralityProcedures.maximalCliqueCentrality(graph, graph.findNode("A")).getRow(0)
                                                       .getValue(1));
    }

}