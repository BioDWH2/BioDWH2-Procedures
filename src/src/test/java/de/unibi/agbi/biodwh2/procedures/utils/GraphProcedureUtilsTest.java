package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

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
        final Edge edgeAB = graph.addEdge(nodeA, nodeB, "eAB");
        final Edge edgeBD = graph.addEdge(nodeB, nodeD, "eBD");
        final Edge edgeCB = graph.addEdge(nodeC, nodeB, "eCB");
        final Edge edgeCE = graph.addEdge(nodeC, nodeE, "eCE");
        final Edge edgeDF = graph.addEdge(nodeD, nodeF, "eDF");
        final Edge edgeED = graph.addEdge(nodeE, nodeD, "eED");
        final Edge edgeEF = graph.addEdge(nodeE, nodeF, "eEF");
        final Edge edgeEE = graph.addEdge(nodeE, nodeE, "eEE");
    }

    @Test
    void dijkstraTest() {
        HashMap<Long, Long> distances = GraphProcedureUtils.dijkstra(graph, graph.findNode("A"), GraphMode.DIRECTED);
        assertTrue(distances.get(graph.findNode("D").getId()) == 2);
        assertTrue(distances.get(graph.findNode("F").getId()) == 3);
        assertTrue(distances.get(graph.findNode("G").getId()) == Long.MAX_VALUE);
    }

    @Test
    void breadthFirstSearchTest() {
        BFSResult result = GraphProcedureUtils.breadthFirstSearch(graph, graph.findNode("A"), GraphMode.UNDIRECTED);
        assertTrue(Collections.frequency(result.getVisitedNodes().values(), true) == 6);
    }

    @Test
    void getOpenNeighborhoodAsSubgraphTest() throws IOException {
        Graph openNeighborHoodA = GraphProcedureUtils.getOpenNeighborhoodAsSubgraph(graph, graph.findNode("B"), GraphMode.UNDIRECTED);
        String[] targetNodeLabels = new String[]{"A", "D", "C"};
        assertTrue(openNeighborHoodA.getNumberOfNodes() == 3);
        assertTrue(targetNodeLabels.length == openNeighborHoodA.getNodeLabels().length);
        assertTrue(Arrays.equals(Arrays.stream(targetNodeLabels).sorted().toArray(), Arrays.stream(openNeighborHoodA.getNodeLabels()).sorted().toArray()));
    }

    @Test
    void findComponentsUndirectedTest() {
        ArrayList<BFSResult> components = GraphProcedureUtils.findComponentsUndirected(graph);

        for(BFSResult component : components) {
            System.out.println("###############################");
            System.out.println(component.getVisitedNodes().size() + " Node(s) in component");
            for(long id : component.getVisitedNodes().keySet()) {
                System.out.println(graph.getNode(id).getLabel());
            }
            System.out.println("Traversal order:");
            for(Edge path : component.getPaths()) {
                System.out.println(path.getLabel());
            }
        }

        assertTrue(components.size() == 2);
        assertTrue(components.get(0).getVisitedNodes().size() == 6);
        assertTrue(components.get(1).getVisitedNodes().size() == 1);
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
        BFSResult maximumConnectedComponent = GraphProcedureUtils.getMaximumConnectedComponent(graph, graph.findNode("5"), GraphMode.UNDIRECTED);
        assertTrue(maximumConnectedComponent.getVisitedNodes().size() == 2);
        assertTrue(maximumConnectedComponent.getPaths().size() == 1);
    }

}





















