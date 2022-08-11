package de.unibi.agbi.biodwh2.procedures.model;

import de.unibi.agbi.biodwh2.core.model.graph.Node;

/**
 * Represents a tuple in a dijkstra priority queue (node id, distance)
 */
public class DistancePair implements Comparable<DistancePair> {
    private final Node node;
    private final long distance;

    public DistancePair(final Node node, final long distance) {
        this.node = node;
        this.distance = distance;
    }

    public Node getNode() {
        return node;
    }

    public long getDistance() {
        return distance;
    }

    @Override
    public int compareTo(final DistancePair o) {
        return Long.compare(this.distance, o.distance);
    }
}
