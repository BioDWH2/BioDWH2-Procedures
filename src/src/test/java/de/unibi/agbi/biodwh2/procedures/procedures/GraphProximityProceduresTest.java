package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.utils.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.GraphProcedureUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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

        Node a1 = merged.addNode("A");
        Node b1 = merged.addNode("B");
        Node b2 = merged.addNode("B");
        Node c = merged.addNode("C");

        merged.addEdge(a1, b1, "a1b1");
        merged.addEdge(a1, c, "a1c");
        merged.addEdge(c, b2, "cb2");
    }

}