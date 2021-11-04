package de.unibi.agbi.biodwh2.procedures.analysis;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphDegreeCentralityTest {
    private Graph graph;
    private GraphDegreeCentrality centrality;

    @BeforeEach
    void setUp() throws IOException {
        graph = Graph.createTempGraph();
        graph.addIndex(IndexDescription.forNode("A", "id", IndexDescription.Type.UNIQUE));
        final Node nodeA1 = graph.addNode("A", "id", 1);
        final Node nodeA2 = graph.addNode("A", "id", 2);
        final Node nodeA3 = graph.addNode("A", "id", 3);
        final Node nodeA4 = graph.addNode("A", "id", 4);
        graph.addEdge(nodeA1, nodeA2, "TO");
        graph.addEdge(nodeA1, nodeA3, "TO");
        graph.addEdge(nodeA1, nodeA4, "TO");
        graph.addEdge(nodeA2, nodeA1, "TO");
        graph.addEdge(nodeA2, nodeA4, "TO");
        centrality = new GraphDegreeCentrality(graph);
    }

    @AfterEach
    void tearDown() {
        if (graph != null)
            graph.close();
    }

    @Test
    void testDegree() {
        assertEquals(4, centrality.degree(graph.findNode("A", "id", 1)));
        assertEquals(3, centrality.degree(graph.findNode("A", "id", 2)));
        assertEquals(1, centrality.degree(graph.findNode("A", "id", 3)));
        assertEquals(2, centrality.degree(graph.findNode("A", "id", 4)));
    }

    @Test
    void testIndegree() {
        assertEquals(1, centrality.indegree(graph.findNode("A", "id", 1)));
        assertEquals(1, centrality.indegree(graph.findNode("A", "id", 2)));
        assertEquals(1, centrality.indegree(graph.findNode("A", "id", 3)));
        assertEquals(2, centrality.indegree(graph.findNode("A", "id", 4)));
    }

    @Test
    void testOutdegree() {
        assertEquals(3, centrality.outdegree(graph.findNode("A", "id", 1)));
        assertEquals(2, centrality.outdegree(graph.findNode("A", "id", 2)));
        assertEquals(0, centrality.outdegree(graph.findNode("A", "id", 3)));
        assertEquals(0, centrality.outdegree(graph.findNode("A", "id", 4)));
    }
}