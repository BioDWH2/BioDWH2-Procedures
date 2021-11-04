package de.unibi.agbi.biodwh2.procedures.analysis;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVMapWrapper;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreDB;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class GraphClosenessCentrality {
    private final Graph graph;

    public GraphClosenessCentrality(final Graph graph) {
        this.graph = graph;
    }

    public double harmonic(final Node node) {
        return harmonic(node.getId());
    }

    public double harmonic(final long nodeId) {
        try {
            Path tempFilePath = Files.createTempFile("harmonic", ".db");
            final MVStoreDB store = new MVStoreDB(tempFilePath.toString());
            final MVMapWrapper<Long, Boolean> visitedMap = store.openMap("visited");
            List<Long> layer;
            List<Long> nextLayer = new LinkedList<>();
            nextLayer.add(nodeId);
            long depth = 0;
            double sum = 0;
            while (nextLayer.size() > 0) {
                layer = nextLayer;
                nextLayer = new LinkedList<>();
                depth++;
                for (final long id : layer) {
                    if (visitedMap.get(id))
                        continue;
                    visitedMap.put(nodeId, true);
                    sum += 1.0 / depth;
                    for (final long adjacentId : graph.getAdjacentNodeIdsForEdgeLabel(nodeId))
                        nextLayer.add(adjacentId);
                }
            }
            store.close();
            FileUtils.safeDelete(tempFilePath);
            return sum / (graph.getNumberOfNodes() - 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
