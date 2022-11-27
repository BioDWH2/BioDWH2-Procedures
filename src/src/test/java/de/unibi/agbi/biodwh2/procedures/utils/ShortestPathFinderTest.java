package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.ArrayList;

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
        graph.addEdge(nodeA, nodeB, "eAB");
        graph.addEdge(nodeB, nodeD, "eBD");
        graph.addEdge(nodeC, nodeB, "eCB");
        graph.addEdge(nodeC, nodeE, "eCE");
        graph.addEdge(nodeD, nodeF, "eDF");
        graph.addEdge(nodeE, nodeD, "eED");
        graph.addEdge(nodeE, nodeF, "eEF");
        graph.addEdge(nodeE, nodeE, "eEE");

        this.shortestPathFinder = new ShortestPathFinder(graph, GraphMode.UNDIRECTED);
    }

    @Test
    void dijkstraSourceTargetTest() {
        final Node source = graph.findNode("A");
        final Node target = graph.findNode("D");
        final DijkstraResult result = shortestPathFinder.dijkstra(source.getId(), target.getId());
        assertEquals(1, result.getDistances().size());
        assertEquals(2, (long) result.getDistances().get(target.getId()));
    }

    @Test
    void dijkstraMultiTargetTestWithSelfToZero() {
        final Node source = graph.findNode("A");
        final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(source.getId(), false);
        assertEquals(0, (long) dijkstraResult.getDistances().get(graph.findNode("A").getId()));
        assertEquals(2, (long) dijkstraResult.getDistances().get(graph.findNode("D").getId()));
        assertEquals(3, (long) dijkstraResult.getDistances().get(graph.findNode("F").getId()));
        assertEquals(Long.MAX_VALUE, (long) dijkstraResult.getDistances().get(graph.findNode("G").getId()));
    }

    @Test
    void dijkstraMultiTargetTestWithSelfToInfinity() {
        final Node source = graph.findNode("A");
        final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(source.getId(), true);
        assertEquals(Long.MAX_VALUE, (long) dijkstraResult.getDistances().get(graph.findNode("A").getId()));
        assertEquals(2, (long) dijkstraResult.getDistances().get(graph.findNode("D").getId()));
        assertEquals(3, (long) dijkstraResult.getDistances().get(graph.findNode("F").getId()));
        assertEquals(Long.MAX_VALUE, (long) dijkstraResult.getDistances().get(graph.findNode("G").getId()));
    }

    @Test
    void findAllShortestPathsWithReachableTarget() {
        final long sourceNodeId = graph.findNode("C").getId();
        final long targetNodeId = graph.findNode("D").getId();
        ArrayList<ArrayList<Long>> allShortestPaths = shortestPathFinder.dijkstraWithAllPossibleShortestPaths(sourceNodeId).getPathsToNode(targetNodeId);
        assertEquals(2, allShortestPaths.size());
        for(ArrayList<Long> path : allShortestPaths) {
            assertEquals(2, path.size());
        }
    }

    @Test
    void findAllShortestPathsWithUnreachableTarget() {
        final long sourceNodeId = graph.findNode("A").getId();
        final long targetNodeId = graph.findNode("G").getId();
        ArrayList<ArrayList<Long>> allShortestPaths = shortestPathFinder.dijkstraWithAllPossibleShortestPaths(sourceNodeId).getPathsToNode(targetNodeId);
        assertEquals(0, allShortestPaths.size());
    }
}