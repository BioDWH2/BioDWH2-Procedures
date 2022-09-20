package de.unibi.agbi.biodwh2.procedures.model;

/**
 * Represents a tuple in a dijkstra priority queue (node id, distance)
 */
public class DistancePair implements Comparable<DistancePair> {
    private final long nodeId;
    private final long distance;

    public DistancePair(final long nodeId, final long distance) {
        this.nodeId = nodeId;
        this.distance = distance;
    }

    public long getNodeId() {
        return nodeId;
    }

    public long getDistance() {
        return distance;
    }

    @Override
    public int compareTo(final DistancePair o) {
        return Long.compare(this.distance, o.distance);
    }
}
