package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultRow;
import de.unibi.agbi.biodwh2.procedures.ResultSet;
import de.unibi.agbi.biodwh2.procedures.factory.ShortestPathFinderFactory;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.ShortestPathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains procedures for network proximity analysis.
 */
public final class GraphProximityProcedures implements RegistryContainer {

    /**
     * Calculates the average length of all shortest paths between the drug targets t ∈ T and the respective closest
     * disease protein in the disease module
     *
     * @param graph                Merged graph containing both drug targets and disease proteins
     * @param labelTarget          Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode                 Graph mode, i.e. directed or undirected
     * @param isModified           Determines whether the modified or unmodified measure is used: In case of a modified
     *                             measure, the shortest distance from a node to itself during dijkstra calculation will
     *                             be set to ∞, else, it will be set to 0
     * @return Result set with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.closest", description = "Calculates the Closest measure for a drug target set and a disease protein set")
    public static ResultSet closest(final BaseGraph graph, final String labelTarget, final String labelDiseaseProteins,
                                    final GraphMode mode, final boolean isModified) {

        ArrayList<Long> allTargetNodeIds = new ArrayList<>();
        Iterable<Node> allGraphNodes = graph.getNodes(labelTarget);
        for(Node node : allGraphNodes) {
            allTargetNodeIds.add(node.getId());
        }

        float sum = 0;
        for(long targetNodeId : allTargetNodeIds) {
            // retrieve distances to all protein nodes and add minimum to accumulated sum
            final ShortestPathFinder shortestPathFinder = ShortestPathFinderFactory.getInstance().get(graph, mode);
            final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(targetNodeId, isModified,
                                                                              labelDiseaseProteins);
            sum += Collections.min(dijkstraResult.getDistances().values());

        }
        sum *= (1.0 / graph.getNumberOfNodes(labelTarget));
        final ResultSet result = new ResultSet("d_c");
        result.addRow(new ResultRow(new String[]{"d_c"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the average length of all shortest paths between drug targets T and disease proteins P.
     *
     * @param graph                Merged graph containing both drug targets and disease proteins
     * @param labelTarget          Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode                 Graph mode, i.e. directed or undirected
     * @return Result set with proximity measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.shortest", description = "Calculates the Shortest measure for a drug target set and a disease protein set")
    public static ResultSet shortest(final BaseGraph graph, final String labelTarget, final String labelDiseaseProteins,
                                     final GraphMode mode) {

        ArrayList<Long> allTargetNodeIds = new ArrayList<>();
        Iterable<Node> allGraphNodes = graph.getNodes(labelTarget);
        for(Node node : allGraphNodes) {
            allTargetNodeIds.add(node.getId());
        }

        float sum = 0;
        for(long targetNodeId : allTargetNodeIds) {
            // calculate all shortest paths to all disease proteins and add them up ...
            float sumShortestPaths = 0;
            final ShortestPathFinder shortestPathFinder = ShortestPathFinderFactory.get(graph, mode);
            final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(targetNodeId, false,
                                                                              labelDiseaseProteins);
            for (Long distance : dijkstraResult.getDistances().values()) {
                sumShortestPaths += distance;
            }
            // ... and add result to outer sum
            sum += (1.0 / graph.getNumberOfNodes(labelDiseaseProteins)) * sumShortestPaths;
        }
        sum *= (1.0 / graph.getNumberOfNodes(labelTarget));
        final ResultSet result = new ResultSet("d_s");
        result.addRow(new ResultRow(new String[]{"d_s"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the Kernel proximity measure by weighting longer shortest paths with a penalty.
     *
     * @param graph               Merged graph containing both drug targets and disease proteins
     * @param labelTargets         Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode                 Graph mode, i.e. directed or undirected
     * @return Result set with kernel measure for drug-disease pair
     */
    @Procedure(name = "analysis.network.proximity.kernel", description = "Calculates the Kernel measure for a drug target set and a disease protein set")
    public static ResultSet kernel(final BaseGraph graph, final String labelTargets, final String labelDiseaseProteins,
                                   final GraphMode mode) {

        ArrayList<Long> allTargetNodeIds = new ArrayList<>();
        Iterable<Node> allGraphNodes = graph.getNodes(labelTargets);
        for(Node node : allGraphNodes) {
            allTargetNodeIds.add(node.getId());
        }

        float sum = 0;
        for(long drugTargetId : allTargetNodeIds) {
            double sumKernel = 0;
            final ShortestPathFinder shortestPathFinder = ShortestPathFinderFactory.getInstance().get(graph, mode);
            final DijkstraResult dijkstraResult = shortestPathFinder.dijkstra(drugTargetId, false,
                                                                              labelDiseaseProteins);
            // add up all distances with exponential penalty ...
            for (Long distance : dijkstraResult.getDistances().values()) {
                sumKernel += (Math.exp(-distance + 1)) / graph.getNumberOfNodes(labelDiseaseProteins);
            }
            // ... and add them to outer sum
            sum += Math.log(sumKernel);
        }
        sum *= ((-1.0) / graph.getNumberOfNodes(labelTargets));
        final ResultSet result = new ResultSet("d_k");
        result.addRow(new ResultRow(new String[]{"d_k"}, new Object[]{sum}));
        return result;
    }

    /**
     * Calculates the separation proximity measure between drug targets T and disease proteins P by computing the
     * shortest path length between all drug targets and the topological center of the disease module.
     *
     * @param graph               Merged graph containing both drug targets and disease proteins
     * @param labelTargets         Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode                 Graph mode, i.e. directed or undirected
     * @return Result set with separation proximity measure
     */
    @Procedure(name = "analysis.network.proximity.separation", description = "Calculates the Separation measure for a drug target set and a disease protein set")
    public static ResultSet separation(final BaseGraph graph, final String labelTargets,
                                       final String labelDiseaseProteins, final GraphMode mode) {

        // calculate modified closest measure for targets and disease proteins (right-hand side of term)
        float modifiedTargets = (float) GraphProximityProcedures.closest(graph, labelTargets, labelTargets, mode, true)
                                                                .getRow(0).getValue(1);
        float modifiedProteins = (float) GraphProximityProcedures.closest(graph, labelDiseaseProteins,
                                                                          labelDiseaseProteins, mode, true).getRow(0)
                                                                 .getValue(1);
        float sumAvgDistance = (modifiedTargets + modifiedProteins) / 2;

        // calculate dispersion (left-hand side of term)
        float closestTargetsProteins = (float) GraphProximityProcedures.closest(graph, labelTargets,
                                                                                labelDiseaseProteins, mode, false)
                                                                       .getRow(0).getValue(1);
        float closestProteinsTargets = (float) GraphProximityProcedures.closest(graph, labelDiseaseProteins,
                                                                                labelTargets, mode, false).getRow(0)
                                                                       .getValue(1);
        long numTargets = graph.getNumberOfNodes(labelTargets);
        long numProteins = graph.getNumberOfNodes(labelDiseaseProteins);
        float dispersion = (numTargets * closestProteinsTargets + numProteins * closestTargetsProteins) /
                           (numTargets + numProteins);

        final ResultSet result = new ResultSet("d_ss");
        result.addRow(new ResultRow(new String[]{"d_ss"}, new Object[]{dispersion - sumAvgDistance}));
        return result;
    }

    /**
     * Calculates the centre measure between drug targets T and disease proteins P.
     *
     * @param graph               Merged graph containing both drug targets and disease proteins
     * @param labelTargets         Label describing the drug target nodes
     * @param labelDiseaseProteins Label describing the disease protein nodes
     * @param mode                 Graph mode, i.e. directed or undirected
     */
    @Procedure(name = "analysis.network.proximity.centre", description = "Calculates the Centre measure for a drug target set and a disease protein set")
    public static ResultSet centre(final BaseGraph graph, final String labelTargets, final String labelDiseaseProteins,
                                   final GraphMode mode) {

        // calculate the topological center of disease module, i.e. the node with the largest closeness centrality in the module
        final Map<Long, Double> closenessForProtein = new HashMap<>();
        for (final Node node : graph.getNodes(labelDiseaseProteins)) {
            ResultSet result = GraphCentralityProcedures.closeness(graph, node, GraphMode.UNDIRECTED,
                                                                   labelDiseaseProteins);
            closenessForProtein.put(node.getId(), (double) result.getRow(0).getValue(1));
        }
        final Node centre = graph.getNode(
                Collections.max(closenessForProtein.entrySet(), Map.Entry.comparingByValue()).getKey());

        // add up all distances from nodes in T to centre node and normalize
        double sum = 0;
        final ShortestPathFinder shortestPathFinder = ShortestPathFinderFactory.get(graph, mode);
        for (final Node drugTarget : graph.getNodes(labelTargets)) {
            long drugTargetId = drugTarget.getId();
            sum += shortestPathFinder.dijkstra(centre.getId(), drugTargetId).getDistances().get(drugTargetId);
        }
        sum *= 1.0 / graph.getNumberOfNodes(labelTargets);

        final ResultSet result = new ResultSet("d_cc");
        result.addRow(new ResultRow(new String[]{"d_cc"}, new Object[]{sum}));
        return result;
    }

}