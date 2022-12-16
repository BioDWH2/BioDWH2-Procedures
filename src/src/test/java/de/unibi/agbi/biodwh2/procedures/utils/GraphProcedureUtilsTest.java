package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphProcedureUtilsTest {

    private Graph graph;

    @BeforeAll
    void setup() throws IOException {
        graph = Graph.createTempGraph();
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
    }

    @Test
    void breadthFirstSearchTest() {
        BFSResult result = GraphProcedureUtils.breadthFirstSearch(graph, graph.findNode("A").getId(),
                                                                  GraphMode.UNDIRECTED);
        assertEquals(6, result.getNodeIds().size());
    }

    @Test
    void getOpenNeighborhoodAsSubgraphTest() throws IOException {
        BaseGraph openNeighborHoodA = GraphProcedureUtils.getOpenNeighborhoodAsSubgraph(graph,
                                                                                        graph.findNode("B").getId(),
                                                                                        GraphMode.UNDIRECTED);
        String[] targetNodeLabels = new String[]{"A", "D", "C"};
        assertEquals(3, openNeighborHoodA.getNumberOfNodes());
        assertEquals(0, openNeighborHoodA.getNumberOfEdges());
        assertEquals(targetNodeLabels.length, openNeighborHoodA.getNodeLabels().length);
        assertArrayEquals(Arrays.stream(targetNodeLabels).sorted().toArray(), Arrays.stream(
                openNeighborHoodA.getNodeLabels()).sorted().toArray());
    }

    @Test
    void findComponentsUndirectedTest() {
        final List<BFSResult> components = GraphProcedureUtils.findComponentsUndirected(graph);
        assertEquals(2, components.size());
        assertEquals(6, components.get(0).getNodeIds().size());
        assertEquals(1, components.get(1).getNodeIds().size());
    }

    @Test
    void findComponentsUndirectedWithMultipleSourcesTest() throws IOException {
        Graph graph = Graph.createTempGraph();
        Node node1 = graph.addNode("1");
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        Node node6 = graph.addNode("6");
        Node node7 = graph.addNode("7");
        graph.addEdge(node1, node2, "e1-2");
        graph.addEdge(node1, node3, "e1-3");
        graph.addEdge(node1, node4, "e1-4");
        graph.addEdge(node3, node4, "e3-4");
        graph.addEdge(node5, node6, "e5-6");

        final List<Long> ids = Arrays.asList(graph.findNode("1").getId(), graph.findNode("4").getId(), graph.findNode("7").getId());
        final List<BFSResult> components = GraphProcedureUtils.findComponentsUndirected(graph, ids);
        assertEquals(2, components.size());
        assertEquals(4, components.get(0).getNodeIds().size());
        assertEquals(1, components.get(1).getNodeIds().size());
    }

    @Test
    void getMaximumConnectedComponentTest() throws IOException {
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
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph,
                                                                                               graph.findNode("5")
                                                                                                    .getId(),
                                                                                               GraphMode.UNDIRECTED);
        assertEquals(2, maximumConnectedComponent.getNodeIds().size());
        assertEquals(1, maximumConnectedComponent.getEdgePathIds().size());
    }

}





















