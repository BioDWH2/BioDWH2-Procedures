package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphCliqueFinderTest {

    @Test
    void findCliquesTest() throws IOException {
        final Graph graph = Graph.createTempGraph();
        final Node nodeA = graph.addNode("A");
        final Node nodeB = graph.addNode("B");
        final Node nodeC = graph.addNode("C");
        final Node nodeD = graph.addNode("D");
        final Node nodeE = graph.addNode("E");
        final Node nodeF = graph.addNode("F");

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

        final GraphCliqueFinder graphCliqueFinder = new GraphCliqueFinder(graph);
        assertEquals(4, graphCliqueFinder.getCliques().size());
    }

}