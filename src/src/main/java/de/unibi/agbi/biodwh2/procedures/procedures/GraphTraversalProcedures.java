package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.model.BFSResult;
import de.unibi.agbi.biodwh2.procedures.utils.GraphProcedureUtils;

import java.util.List;

/**
 * Contains procedures for traversing graphs and finding components.
 */
public class GraphTraversalProcedures implements RegistryContainer {

    /**
     * Finds all connected components of a graph via breadth-first search.
     * @param graph Graph whose components are to be found
     * @return Result containing a list of nodes and edges for each component
     */
    @Procedure(name = "analysis.network.traversal.components", description = "Finds all components in a given graph")
    public static ResultSet components(final BaseGraph graph) {
        final List<BFSResult> components = GraphProcedureUtils.findComponentsUndirected(graph);
        ResultSet result = new ResultSet("nodes", "edges");
        for(BFSResult component : components) {
            result.addRow(new ResultRow(new String[]{"nodes", "edges"}, new Object[]{component.getNodeIds(), component.getEdgePathIds()}));
        }
        return result;
    }

}
