package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains a result of a breadth first search, i.e. the nodes that have been visited
 * as well as all paths that have been traversed.
 */
public class BFSResult {

    /**
     * All nodes that have been traversed in course of the search.
     */
    private HashMap<Long, Boolean> visitedNodes;
    /**
     * All edge paths in order of traversal
     */
    private ArrayList<Edge> paths;

    public BFSResult(HashMap<Long, Boolean> visitedNodes, ArrayList<Edge> paths) {
        this.visitedNodes = visitedNodes;
        this.paths = paths;
    }

    public HashMap<Long, Boolean> getVisitedNodes() {
        return visitedNodes;
    }

    public ArrayList<Edge> getPaths() {
        return paths;
    }
}
