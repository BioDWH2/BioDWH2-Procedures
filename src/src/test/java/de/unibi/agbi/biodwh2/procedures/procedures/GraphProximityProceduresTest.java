package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphProximityProceduresTest {

    private Graph drugTargets;
    private Graph diseaseProteins;
    private Graph merged;

    @BeforeAll
    void setup() throws IOException {
        drugTargets = Graph.createTempGraph();
        diseaseProteins = Graph.createTempGraph();
        merged = Graph.createTempGraph();

        Node a = merged.addNode("A");
        Node b = merged.addNode("B");
        Node c = merged.addNode("C");
        Node d = merged.addNode("D");
        Node e = merged.addNode("E");

        merged.addEdge(a, b, "a|b");
        merged.addEdge(a, c, "a|c");
        merged.addEdge(b, c, "b|c");
        merged.addEdge(b, d, "b|d");
        merged.addEdge(b, e, "b|e");
    }

    @Test
    void testCentre() {
        HashMap<Long, Double> closenessCentralities = new HashMap<>();
        for(Node node : merged.getNodes()) {
            ResultSet result = GraphCentralityProcedures.closeness(merged, node, GraphMode.UNDIRECTED);
            closenessCentralities.put(node.getId(), (double) result.getRow(0).getValue(1));
        }
        Long id = Collections.max(closenessCentralities.entrySet(), Map.Entry.comparingByValue()).getKey();

    }

}








