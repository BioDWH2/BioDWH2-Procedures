package de.unibi.agbi.biodwh2.procedures.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Holds the result of Dijkstra's algorithm, i.e. the distances between all nodes and
 * (in case of a single-source/single-target approach) the shortest path between source and target node.
 */
public class DijkstraResult {

    /**
     * Distances from a source node to either a target node, or all other nodes in the graph
     */
    private final Map<Long, Long> distances;
    /**
     * A path from source node to target node (only if a target node has been explicitly specified)
     */
    private final ArrayList<Long> path;

    public DijkstraResult(final Map<Long, Long> distances) {
        this.distances = distances;
        this.path = new ArrayList<>();
    }

    public DijkstraResult(final Map<Long, Long> distances, final ArrayList<Long> path) {
        this.distances = distances;
        this.path = path;
    }

    /**
     * Determines whether a node lies on a path revealed by Dijkstra. Only returns true, if
     * the node in question is neither the last nor the first node on the path
     * @param nodeId ID of the specified node
     */
    public boolean pathPassesTroughNode(long nodeId) {
        return path.contains(nodeId) && path.get(0) != nodeId && path.get(path.size() - 1) != nodeId;
    }

    public Map<Long, Long> getDistances() { return distances; }
    public ArrayList<Long> getPath() { return path; }

}
