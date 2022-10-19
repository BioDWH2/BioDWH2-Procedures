package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GraphTraversalProceduresTest {

    @Test
    void componentsTest() throws IOException {
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
        Node node10 = graph.addNode("10");
        Node node11 = graph.addNode("11");
        Node node12 = graph.addNode("12");
        graph.addEdge(node1, node7, "e1-7");
        graph.addEdge(node2, node7, "e2-7");
        graph.addEdge(node2, node4, "e2-4");
        graph.addEdge(node3, node6, "e3-6");
        graph.addEdge(node4, node7, "e4-7");
        graph.addEdge(node5, node11, "e5-11");
        graph.addEdge(node6, node12, "e6-12");
        graph.addEdge(node7, node8, "e7-8");
        graph.addEdge(node7, node9, "e7-9");
        graph.addEdge(node7, node10, "e7-10");

        ResultSet results = GraphTraversalProcedures.components(graph);
        assertEquals(3, results.getRowCount());

        ArrayList edgesLargestComponent = (ArrayList) results.getRow(0).getValue("edges");
        ArrayList nodesLargestComponent = (ArrayList) results.getRow(0).getValue("nodes");
        assertEquals(7, edgesLargestComponent.size());
        assertEquals(7, nodesLargestComponent.size());

        ArrayList edgesSmallestComponent = (ArrayList) results.getRow(2).getValue("edges");
        ArrayList nodesSmallestComponent = (ArrayList) results.getRow(2).getValue("nodes");
        assertEquals(1, edgesSmallestComponent.size());
        assertEquals(2, nodesSmallestComponent.size());

    }

}