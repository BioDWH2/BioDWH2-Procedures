package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShortestPathFinderTest {

    private ShortestPathFinder shortestPathFinder;
    private Graph graph;

    @BeforeAll
    void setup() throws IOException {
        this.graph = Graph.createTempGraph();
        final Node nodeA = graph.addNode("A");
        final Node nodeB = graph.addNode("B");
        final Node nodeC = graph.addNode("C");
        final Node nodeD = graph.addNode("D");
        final Node nodeE = graph.addNode("E");
        final Node nodeF = graph.addNode("F");
        final Node nodeG = graph.addNode("G");
        final Edge edgeAB = graph.addEdge(nodeA, nodeB, "eAB");
        final Edge edgeBD = graph.addEdge(nodeB, nodeD, "eBD");
        final Edge edgeCB = graph.addEdge(nodeC, nodeB, "eCB");
        final Edge edgeCE = graph.addEdge(nodeC, nodeE, "eCE");
        final Edge edgeDF = graph.addEdge(nodeD, nodeF, "eDF");
        final Edge edgeED = graph.addEdge(nodeE, nodeD, "eED");
        final Edge edgeEF = graph.addEdge(nodeE, nodeF, "eEF");
        final Edge edgeEE = graph.addEdge(nodeE, nodeE, "eEE");

        this.shortestPathFinder = new ShortestPathFinder(graph);
    }

    @Test
    void dijkstraSourceTargetTest() {
        Node source = graph.findNode("A");
        Node target = graph.findNode("D");
        HashMap<Long, Long> distances = shortestPathFinder.dijkstra(graph, source, target, GraphMode.UNDIRECTED);
        assertTrue(distances.size() == 1);
        assertTrue(distances.get(target.getId()) == 2);
    }

    @Test
    void dijkstraMultiTargetTestWithSelfToZero() {
        HashMap<Long, Long> distances = shortestPathFinder.dijkstra(graph, graph.findNode("A"), GraphMode.DIRECTED, false);
        assertTrue(distances.get(graph.findNode("A").getId()) == 0);
        assertTrue(distances.get(graph.findNode("D").getId()) == 2);
        assertTrue(distances.get(graph.findNode("F").getId()) == 3);
        assertTrue(distances.get(graph.findNode("G").getId()) == Long.MAX_VALUE);
    }

    @Test
    void dijkstraMultiTargetTestWithSelfToInfinity() {
        HashMap<Long, Long> distances = shortestPathFinder.dijkstra(graph, graph.findNode("A"), GraphMode.DIRECTED, true);
        assertTrue(distances.get(graph.findNode("A").getId()) == Long.MAX_VALUE);
        assertTrue(distances.get(graph.findNode("D").getId()) == 2);
        assertTrue(distances.get(graph.findNode("F").getId()) == 3);
        assertTrue(distances.get(graph.findNode("G").getId()) == Long.MAX_VALUE);
    }

}