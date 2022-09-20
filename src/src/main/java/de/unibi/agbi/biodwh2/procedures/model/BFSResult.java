package de.unibi.agbi.biodwh2.procedures.model;

import java.util.List;
import java.util.Map;

/**
 * Contains a result of a breadth first search, i.e. the nodes that have been visited as well as all paths that have
 * been traversed.
 */
public class BFSResult {

    /**
     * All nodes that have been traversed in course of the search.
     */
    private final Map<Long, Boolean> visitedNodes;
    /**
     * All edge paths in order of traversal
     */
    private final List<Long> edgePathIds;

    public BFSResult(final Map<Long, Boolean> visitedNodes, final List<Long> edgePathIds) {
        this.visitedNodes = visitedNodes;
        this.edgePathIds = edgePathIds;
    }

    public Map<Long, Boolean> getVisitedNodes() {
        return visitedNodes;
    }

    public List<Long> getEdgePathIds() {
        return edgePathIds;
    }
}
