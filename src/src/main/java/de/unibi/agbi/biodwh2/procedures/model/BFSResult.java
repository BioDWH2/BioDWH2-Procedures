package de.unibi.agbi.biodwh2.procedures.model;

import java.util.List;

/**
 * Contains a result of a breadth first search, i.e. the nodes that have been visited as well as all paths that have
 * been traversed.
 */
public class BFSResult {

    /**
     * All edge paths in order of traversal
     */
    private final List<Long> edgePathIds;
    /**
     * All nodes that were visited in the course of bfs.
     */
    private final List<Long> nodeIds;

    public BFSResult(final List<Long> edgePathIds, final List<Long> nodeIds) {
        this.edgePathIds = edgePathIds;
        this.nodeIds = nodeIds;
    }

    public List<Long> getNodeIds() { return nodeIds; }
    public List<Long> getEdgePathIds() {
        return edgePathIds;
    }
}
