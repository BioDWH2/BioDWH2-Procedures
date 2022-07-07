package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import de.unibi.agbi.biodwh2.procedures.utils.GraphMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

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
        final Edge edgeAB = graphDisconnected.addEdge(nodeA, nodeB, "eAB");
        final Edge edgeBD = graphDisconnected.addEdge(nodeB, nodeD, "eBD");
        final Edge edgeCB = graphDisconnected.addEdge(nodeC, nodeB, "eCB");
        final Edge edgeCE = graphDisconnected.addEdge(nodeC, nodeE, "eCE");
        final Edge edgeDF = graphDisconnected.addEdge(nodeD, nodeF, "eDF");
        final Edge edgeED = graphDisconnected.addEdge(nodeE, nodeD, "eED");
        final Edge edgeEF = graphDisconnected.addEdge(nodeE, nodeF, "eEF");
        final Edge edgeEE = graphDisconnected.addEdge(nodeE, nodeE, "eEE");

        graphConnected = Graph.createTempGraph();
        final Node nodeA2 = graphConnected.addNode("A");
        final Node nodeB2 = graphConnected.addNode("B");
        final Node nodeC2 = graphConnected.addNode("C");
        final Node nodeD2 = graphConnected.addNode("D");
        final Node nodeE2 = graphConnected.addNode("E");
        final Node nodeF2 = graphConnected.addNode("F");
        final Node nodeG2 = graphConnected.addNode("G");
        final Edge edgeAC2 = graphConnected.addEdge(nodeA2, nodeC2, "eAC");
        final Edge edgeAB2 = graphConnected.addEdge(nodeA2, nodeB2, "eAB");
        final Edge edgeAD2 = graphConnected.addEdge(nodeA2, nodeD2, "eAD");
        final Edge edgeCF2 = graphConnected.addEdge(nodeC2, nodeF2, "eCF");
        final Edge edgeFD2 = graphConnected.addEdge(nodeF2, nodeD2, "eFD");
        final Edge edgeDE2 = graphConnected.addEdge(nodeD2, nodeE2, "eDE");
        final Edge edgeFG2 = graphConnected.addEdge(nodeF2, nodeG2, "eFG");
        final Edge edgeBE2 = graphConnected.addEdge(nodeB2, nodeE2, "eBE");
        final Edge edgeEG2 = graphConnected.addEdge(nodeE2, nodeG2, "eEG");
    }

    @Test
    void degreeTest() {
        assertTrue((long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("E").getId()).getRow(0).getValue(1) == 5);
        assertTrue((long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("A").getId()).getRow(0).getValue(1) == 1);
        assertTrue((long) GraphCentralityProcedures.degree(graphDisconnected, graphDisconnected.findNode("G").getId()).getRow(0).getValue(1) == 0);
    }

    @Test
    void closenessTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node nodeA = graph.addNode("A");
        Node nodeB = graph.addNode("B");
        Node nodeC = graph.addNode("C");
        Node nodeD = graph.addNode("D");
        Node nodeE = graph.addNode("E");
        Node nodeF = graph.addNode("F");
        Node nodeG = graph.addNode("G");
        Node nodeH = graph.addNode("H");
        Edge edgeAB = graph.addEdge(nodeA, nodeB, "eAB");
        Edge edgeBC = graph.addEdge(nodeB, nodeC, "eBC");
        Edge edgeCD = graph.addEdge(nodeC, nodeD, "eAB");
        Edge edgeDE = graph.addEdge(nodeD, nodeE, "eDE");
        Edge edgeDH = graph.addEdge(nodeD, nodeH, "eDH");
        Edge edgeEF = graph.addEdge(nodeE, nodeF, "eEF");
        Edge edgeEG = graph.addEdge(nodeE, nodeG, "eEG");
        Edge edgeEH = graph.addEdge(nodeE, nodeH, "eEH");
        Edge edgeFG = graph.addEdge(nodeF, nodeG, "eFG");
        Edge edgeHG = graph.addEdge(nodeH, nodeG, "eHG");

        double degD = (double) GraphCentralityProcedures.closeness(graph, graph.findNode("D"), GraphMode.UNDIRECTED).getRow(0).getValue(1);
        double degA = (double) GraphCentralityProcedures.closeness(graph, graph.findNode("A"), GraphMode.UNDIRECTED).getRow(0).getValue(1);
        assertTrue(degD > degA);
    }

    @Test
    void eccentricityTest() {
        assertTrue((double) GraphCentralityProcedures.eccentricity(graphConnected, graphConnected.findNode("A"), GraphMode.UNDIRECTED).getRow(0).getValue(1) == 1.0 / 3);
        assertTrue((double) GraphCentralityProcedures.eccentricity(graphConnected, graphConnected.findNode("B"), GraphMode.UNDIRECTED).getRow(0).getValue(1) == 1.0/3);
        assertTrue((double) GraphCentralityProcedures.eccentricity(graphConnected, graphConnected.findNode("D"), GraphMode.UNDIRECTED).getRow(0).getValue(1) == 1.0/2);
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
        assertTrue((int) GraphCentralityProcedures.maximumNeighborhoodComponent(graph, graph.findNode("5"), GraphMode.UNDIRECTED).getRow(0).getValue(1) == 2);
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
        assertTrue((double) GraphCentralityProcedures.densityOfMaximumNeighborhoodComponent(graph, graph.findNode("5"), GraphMode.UNDIRECTED, 1.7).getRow(0).getValue(1) == 0.3077861033362291);
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

        assertTrue((int) GraphCentralityProcedures.maximalCliqueCentrality(graph, graph.findNode("A")).getRow(0).getValue(1) == 3);

    }
}
