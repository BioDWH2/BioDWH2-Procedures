package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.utils.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.GraphProcedureUtils;
import de.unibi.agbi.biodwh2.procedures.utils.ShortestPathFinder;

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
     * @param labelTarget Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode Graph mode, i.e. directed or undirected
     * @param isModified Determines whether the modified or unmodified measure is used: In case of a modified measure, the
     *                   shortest distance from a node to itself during dijkstra calculation will be set to ∞, else, it
     *                   will be set to 0
     * @return Result set with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.closest", signature = "TODO",
            description = "Calculates the Closest measure for a drug target set and a disease protein set")
    public static ResultSet closest(final Graph merged, final String labelTarget, final String labelDiseaseProteins, final GraphMode mode, final boolean isModified) {
        float sum = 0;
        for(Node targetNode : merged.getNodes(labelTarget)) {
            // retrieve distances to all protein nodes and add minimum to accumulated sum
            ShortestPathFinder shortestPathFinder = new ShortestPathFinder(merged);
            HashMap<Long, Long> distances = shortestPathFinder.dijkstra(merged, targetNode, mode, isModified, labelDiseaseProteins);
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
     * @param labelTarget Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode Graph mode, i.e. directed or undirected
     * @return Result set with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.shortest", signature = "TODO",
            description = "Calculates the Shortest measure for a drug target set and a disease protein set")
    public static ResultSet shortest(final Graph merged, final String labelTarget, final String labelDiseaseProteins, final GraphMode mode) {
        float sum = 0;
        for(Node targetNode : merged.getNodes(labelTarget)) {
            // calculate all shortest paths to all disease proteins and add them up ...
            float sumShortestPaths = 0;
            ShortestPathFinder shortestPathFinder = new ShortestPathFinder(merged);
            HashMap<Long, Long> distances = shortestPathFinder.dijkstra(merged, targetNode, mode, false, labelDiseaseProteins);
            for(Long distance : distances.values()) {
                sumShortestPaths += distance;
            }
            // ... and add result to outer sum
            sum += (1 / merged.getNumberOfNodes(labelDiseaseProteins)) * sumShortestPaths;
        }
        sum *= (1 / merged.getNumberOfNodes(labelTarget));
        final ResultSet result = new ResultSet("d_s");
        result.addRow(new ResultRow(new String[]{"d_s"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the Kernel proximity measure by weighting long shortest paths with a penalty.
     * @param merged Merged graph containing both drug targets and disease proteins
     * @param labelTargets Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode Graph mode, i.e. directed or undirected
     * @return Result set with kernel measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.kernel", signature = "TODO",
            description = "Calculates the Kernel measure for a drug target set and a disease protein set")
    public static ResultSet kernel(final Graph merged, final String labelTargets, final String labelDiseaseProteins, final GraphMode mode) {
        float sum = 0;
        for(Node drugTarget : merged.getNodes(labelTargets)) {
            double sumKernel = 0;
            ShortestPathFinder shortestPathFinder = new ShortestPathFinder(merged);
            HashMap<Long, Long> distances = shortestPathFinder.dijkstra(merged, drugTarget, mode, false, labelDiseaseProteins);
            // add up all distances with exponential penalty ...
            for(Long distance : distances.values()) {
                sumKernel += (Math.exp(- distance + 1)) / merged.getNumberOfNodes(labelDiseaseProteins);
            }
            // ... and add them to outer sum
            sum += Math.log(sumKernel);
        }
        sum *= ((-1) / merged.getNumberOfNodes(labelTargets));
        final ResultSet result = new ResultSet("d_k");
        result.addRow(new ResultRow(new String[]{"d_k"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the separation proximity measure between drug targets T and disease proteins P.
     * @param merged Merged graph containing both drug targets and disease proteins
     * @param labelTargets Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode Graph mode, i.e. directed or undirected
     * @return Result set with separation proximity measure
     */
    @Procedure(name = "analysis.network.proximity.separation", signature = "TODO",
            description = "Calculates the Separation measure for a drug target set and a disease protein set")
    public static ResultSet separation(final Graph merged, final String labelTargets, final String labelDiseaseProteins, final GraphMode mode) {

        // calculate modified closest measure for targets and disease proteins (right-hand side of term)
        float modifiedTargets = (float) GraphProximityProcedures.closest(merged, labelTargets, labelTargets, mode, true).getRow(0).getValue(1);
        float modifiedProteins = (float) GraphProximityProcedures.closest(merged, labelDiseaseProteins, labelDiseaseProteins, mode, true).getRow(0).getValue(1);
        float sumAvgDistance = (modifiedTargets + modifiedProteins) / 2;

        // calculate dispersion (left-hand side of term)
        float closestTargetsProteins = (float) GraphProximityProcedures.closest(merged, labelTargets, labelDiseaseProteins, mode, false).getRow(0).getValue(1);
        float closestProteinsTargets = (float) GraphProximityProcedures.closest(merged, labelDiseaseProteins, labelTargets, mode, false).getRow(0).getValue(1);
        long numTargets = merged.getNumberOfNodes(labelTargets);
        long numProteins = merged.getNumberOfNodes(labelDiseaseProteins);
        float dispersion = (numTargets * closestProteinsTargets + numProteins * closestTargetsProteins) / (numTargets + numProteins);

        ResultSet result = new ResultSet("d_ss");
        result.addRow(new ResultRow(new String[]{"d_ss"}, new Object[]{dispersion - sumAvgDistance}));
        return result;
    }

    @Procedure(name = "analysis.network.proximity.centre", signature = "TODO",
            description = "Calculates the Centre measure for a drug target set and a disease protein set")
    public static ResultSet centre(Graph drugTargets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }


}
