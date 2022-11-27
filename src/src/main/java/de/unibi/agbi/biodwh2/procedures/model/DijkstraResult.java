package de.unibi.agbi.biodwh2.procedures.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Holds the result of Dijkstra's algorithm, i.e. the distances between all nodes involved and
 * (in case multiple shortest paths were found) the information about the parents for each node.
 * TODO: include parents hierarchy in first constructor as well
 */
public class DijkstraResult {

    /**
     * Distances from a source node to either a target node, or all other nodes in the graph
     */
    private final Map<Long, Long> distances;
    /**
     * List of possible parents for each node in the result
     */
    private Map<Long, ArrayList<Long>> parents;

    public DijkstraResult(final Map<Long, Long> distances) {
        this.distances = distances;
    }

    public DijkstraResult(final Map<Long, Long> distances, Map<Long, ArrayList<Long>> parents) {
        this.distances = distances;
        this.parents = parents;
    }

    /**
     * Auxiliary function for building up the path list for a node. Applies the recursive
     * parent search (see below) and reverses the path list, since the recursive traversal is made
     * from path end to path start.
     * @param nodeId ID of the target node
     * @return List of all paths to the target node
     */
    public ArrayList<ArrayList<Long>> getPathsToNode(long nodeId) {
        ArrayList<ArrayList<Long>> paths = new ArrayList<>();
        constructPaths(paths, nodeId, new ArrayList<>());
        for(ArrayList<Long> path : paths) {
            Collections.reverse(path);
        }
        return paths;
    }

    /**
     * Creates all possible shortest paths to a node by recursively traversing the list of its parents.
     * @param paths Ultimately holds all possible paths to the target node
     * @param nodeId ID of the target node
     * @param path Current path
     */
    private void constructPaths(ArrayList<ArrayList<Long>> paths, final long nodeId, ArrayList<Long> path) {
        if(parents.get(nodeId).contains( - 1L)) {
            paths.add(new ArrayList<>(path));
            return;
        }
        for(long id : parents.get(nodeId)) {
            path.add(nodeId);
            constructPaths(paths, id, path);
            path.remove(path.size() - 1);
        }
    }


    public Map<Long, Long> getDistances() { return distances; }
    public Map<Long, ArrayList<Long>> getParents() { return parents; }

}
