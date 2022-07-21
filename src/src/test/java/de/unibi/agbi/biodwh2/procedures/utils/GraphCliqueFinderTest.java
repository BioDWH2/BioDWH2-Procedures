package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GraphCliqueFinderTest {

    @Test
    void findCliquesTest() throws IOException {
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

        ArrayList<Node> nodes = new ArrayList<>();
        for(Node node : graph.getNodes()) {
            nodes.add(node);
        }

        GraphCliqueFinder graphCliqueFinder = new GraphCliqueFinder(graph);
        graphCliqueFinder.findCliques(graph, new ArrayList<>(), nodes, new ArrayList<>(), 0);

        for(ArrayList<Node> clique : graphCliqueFinder.getCliques()) {
            System.out.println("################## NEW CLIQUE FOUND ##################");
            for(Node node : clique) {
                System.out.print(" " + node.getLabel() + " ");
            }
            System.out.println();
        }

        assertTrue(graphCliqueFinder.getCliques().size() == 4);
    }

}