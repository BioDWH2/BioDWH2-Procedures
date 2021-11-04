package de.unibi.agbi.biodwh2.procedures.analysis;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public final class GraphDegreeCentrality {
    private final Graph graph;

    public GraphDegreeCentrality(final Graph graph) {
        this.graph = graph;
    }

    public long degree(final Node node) {
        return degree(node.getId());
    }

    public long degree(final long nodeId) {
        return indegree(nodeId) + outdegree(nodeId);
    }

    public long indegree(final Node node) {
        return indegree(node.getId());
    }

    public long indegree(final long nodeId) {
        long counter = 0;
        for (final Edge edge : graph.findEdges(Edge.TO_ID_FIELD, nodeId))
            counter++;
        return counter;
    }

    public long outdegree(final Node node) {
        return outdegree(node.getId());
    }

    public long outdegree(final long nodeId) {
        long counter = 0;
        for (final Edge edge : graph.findEdges(Edge.FROM_ID_FIELD, nodeId))
            counter++;
        return counter;
    }
}
