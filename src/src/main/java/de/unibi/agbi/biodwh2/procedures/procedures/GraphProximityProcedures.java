package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.utils.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.GraphProcedureUtils;

import java.util.Collections;
import java.util.HashMap;

/**
 * Contains procedures for network proximity analysis.
 */
public final class GraphProximityProcedures implements RegistryContainer {

    /**
     * Calculates the average length of all shortest paths between the drug targets
     * t ∈ T and the respective closest disease protein in the disease module
     * @param merged Merged graph containing both drug targets and disease proteins
     * @param mode Graph mode, i.e. directed or undirected
     * @return Result row with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.closest", signature = "TODO",
            description = "Calculates the Closest measure for a drug target set and a disease protein set")
    public static ResultSet closest(final Graph merged, final String labelTarget, final String labelDiseaseProteins, final GraphMode mode) {
        float sum = 0;
        for(Node targetNode : merged.getNodes(labelTarget)) {
            // retrieve distances to all protein nodes and add minimum to accumulated sum
            HashMap<Long, Long> distances = GraphProcedureUtils.dijkstra(merged, targetNode, mode, labelDiseaseProteins);
            sum += Collections.min(distances.values());
        }
        sum *= (1 / merged.getNumberOfNodes(labelTarget));
        final ResultSet result = new ResultSet("d_c");
        result.addRow(new ResultRow(new String[]{"d_c"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the average length of all shortest paths between drug targets T and disease proteins P.
     * @param merged Merged graph containing both drug targets and disease proteins
     * @param mode Graph mode, i.e. directed or undirected
     * @return Result row with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.shortest", signature = "TODO",
            description = "Calculates the Shortest measure for a drug target set and a disease protein set")
    public static ResultSet shortest(final Graph merged, final String labelTargets, final String labelDiseaseProteins, final GraphMode mode) {
        float sum = 0;
        for(Node targetNode : merged.getNodes(labelTargets)) {
            // calculate all shortest paths to all disease proteins and add them up ...
            float sumShortestPaths = 0;
            HashMap<Long, Long> distances = GraphProcedureUtils.dijkstra(merged, targetNode, mode, labelDiseaseProteins);
            for(Long distance : distances.values()) {
                sumShortestPaths += distance;
            }
            sumShortestPaths *= (1 / merged.getNumberOfNodes(labelDiseaseProteins));
            // ... and add result to outer sum
            sum += sumShortestPaths;
        }
        sum *= (1 / merged.getNumberOfNodes(labelTargets));
        final ResultSet result = new ResultSet("d_s");
        result.addRow(new ResultRow(new String[]{"d_s"}, new Object[]{sum}));
        return result;
    }

    @Procedure(name = "analysis.network.proximity.kernel", signature = "TODO",
            description = "Calculates the Kernel measure for a drug target set and a disease protein set")
    public static ResultSet kernel(Graph drugTargets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.centre", signature = "TODO",
            description = "Calculates the Centre measure for a drug target set and a disease protein set")
    public static ResultSet centre(Graph drugTargets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.separation", signature = "TODO",
            description = "Calculates the Separation measure for a drug target set and a disease protein set")
    public static ResultSet separation(Graph drugTargets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

}
