package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.DistancePair;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Finds shortest paths in a graph
 * <p>
 * TODO:
 *  - create result containers instead of hash maps (distance mappings, hop nodes, ...)
 */
public class ShortestPathFinder {

    private final BaseGraph graph;
    private final GraphMode mode;
    private ConcurrentHashMap<Long, DijkstraResult> cache;

    public ShortestPathFinder(final BaseGraph graph, final GraphMode mode) {
        this.graph = graph;
        this.mode = mode;
        this.cache = new ConcurrentHashMap<>();
    }

    public ShortestPathFinder(final BaseGraph graph) {
        this(graph, GraphMode.UNDIRECTED);
    }

    /**
     * Computes the shortest path from a given source to a given target using Dijkstra's algorithm.
     * @param sourceNodeId The source node id
     * @param targetNodeId The target node id
     * @return Maps the path length from source to target
     */
    public DijkstraResult dijkstra(final long sourceNodeId, final long targetNodeId) {

        final Map<Long, Long> distances = new HashMap<>();

        for (final Node node : graph.getNodes()) {
            if (node.getId() != sourceNodeId) {
                distances.put(node.getId(), Long.MAX_VALUE);
            }
        }
        distances.put(sourceNodeId, 0L);
        final Queue<DistancePair> queue = new PriorityQueue<>();
        queue.add(new DistancePair(sourceNodeId, 0));

        while (!queue.isEmpty()) {

            // get next node with the smallest distance
            final DistancePair pair = queue.poll();
            long currentId = pair.getNodeId();
            long distanceCurrent = pair.getDistance();

            // Target was reached, stop computation
            if (currentId == targetNodeId)
                break;

            final List<Long> neighbors = GraphProcedureUtils.getNeighbors(graph, currentId, mode);
            for (final Long neighborId : neighbors) {
                if (distances.get(neighborId) > (distanceCurrent + 1)) {
                    distances.put(neighborId, distanceCurrent + 1);
                    queue.add(new DistancePair(neighborId, distances.get(neighborId)));
                }
            }
        }

        // filter result map so it only contains the source-target-pair
        distances.keySet().removeIf(key -> !key.equals(targetNodeId));

        DijkstraResult result = new DijkstraResult(distances);
        cache.put(sourceNodeId, result);
        return result;
    }

    /**
     * Finds the lengths of all shortest paths from a source node to all other nodes in a graph using Dijkstra's
     * algorithm.
     * @param sourceNodeId    Starting node id
     * @param setSelfInfinity Determines whether the distance from a node to itself will be set to ∞ (Please note that
     *                        this is not the "actual infinity value", but the max value for the Long instance!)
     * @param labels          Filters the algorithm's output by specific node labels (optional parameter)
     * @return A mapping showing all nodes and their shortest paths from source node
     */
    public DijkstraResult dijkstra(final long sourceNodeId, final boolean setSelfInfinity, final String... labels) {

        // init distance mapping and node queue
        final HashMap<Long, Long> distances = new HashMap<>();

        for (final Node node : graph.getNodes()) {
            if (!Objects.equals(node.getId(), sourceNodeId)) {
                distances.put(node.getId(), Long.MAX_VALUE);
            }
        }
        distances.put(sourceNodeId, 0L);
        final PriorityQueue<DistancePair> queue = new PriorityQueue<>();
        queue.add(new DistancePair(sourceNodeId, 0));

        while (!queue.isEmpty()) {

            // remove head of queue and set as current node
            final DistancePair current = queue.poll();
            long currentNodeId = current.getNodeId();

            long currentDistance = current.getDistance();
            final List<Long> neighbors = GraphProcedureUtils.getNeighbors(graph, currentNodeId, mode);

            // For each adjacent neighbor: Update distance if required
            for (final Long neighborId : neighbors) {
                if (distances.get(neighborId) > (currentDistance + 1)) {
                    distances.put(neighborId, currentDistance + 1);
                    queue.add(new DistancePair(neighborId, distances.get(neighborId)));
                }
            }
        }

        // Set distance from node to itself to "infinity" if desired
        if (setSelfInfinity) {
            distances.put(sourceNodeId, Long.MAX_VALUE);
        }

        final List<String> labelsList = Arrays.asList(labels);
        if (!labelsList.isEmpty()) {
            distances.entrySet().removeIf(entry -> !labelsList.contains(graph.getNode(entry.getKey()).getLabel()));
        }

        DijkstraResult result = new DijkstraResult(distances);
        cache.put(sourceNodeId, result);
        return result;
    }

    /**
     * Modified version of dijkstra's algorithm to find all possible shortest paths between a source node all other nodes
     * of a graph.
     * @param sourceNodeId Source node ID
     * @return A list consisting of multiple paths (= lists containing the IDs of all nodes on the path)
     */
    public DijkstraResult dijkstraWithAllPossibleShortestPaths(final long sourceNodeId) {

        Map<Long, Long> distances = new HashMap<>();
        Map<Long, ArrayList<Long>> parents = new HashMap<>();

        // distance = 0 and no parent nodes for source node
        distances.put(sourceNodeId, 0L);
        ArrayList<Long> parentsSource = new ArrayList<>();
        parentsSource.add(- 1L);
        parents.put(sourceNodeId, parentsSource);

        // initialize distances and parents lists for other nodes
        for(final Node node : graph.getNodes()) {
            if(!Objects.equals(node.getId(), sourceNodeId)) {
                distances.put(node.getId(), Long.MAX_VALUE);
                parents.put(node.getId(), new ArrayList<>());
            }
        }

        final PriorityQueue<DistancePair> queue = new PriorityQueue<>();
        queue.add(new DistancePair(sourceNodeId, 0));

        while(!queue.isEmpty()) {

            final DistancePair current = queue.poll();
            long currentNodeId = current.getNodeId();
            long currentDistance = current.getDistance();
            final List<Long> neighbors = GraphProcedureUtils.getNeighbors(graph, currentNodeId, mode);

            for(long neighborId : neighbors) {
                if(distances.get(neighborId) > (currentDistance + 1)) {
                    // unique shortest path was found
                    distances.put(neighborId, currentDistance + 1);
                    queue.add(new DistancePair(neighborId, distances.get(neighborId)));
                    ArrayList<Long> parentsNeighbor = parents.get(neighborId);
                    parentsNeighbor.clear();
                    parentsNeighbor.add(currentNodeId);
                    parents.put(neighborId, parentsNeighbor);
                } else if(distances.get(neighborId) == (currentDistance + 1)) {
                    // path with equal cost was found (-> not unique)
                    ArrayList<Long> parentsNeighbor = parents.get(neighborId);
                    parentsNeighbor.add(currentNodeId);
                    parents.put(neighborId, parentsNeighbor);
                }
            }
        }
        DijkstraResult result = new DijkstraResult(distances, parents);
        cache.put(sourceNodeId, result);
        return result;
    }

    /**
     * Determines whether a node lies on a path revealed by Dijkstra. Only returns true, if
     * the node in question is not the endpoint
     * @param nodeId ID of the specified node
     */
    public static boolean pathPassesTroughNode(ArrayList<Long> path, long nodeId) {
        return path.contains(nodeId) && !Objects.equals(path.get(path.size() - 1), nodeId);
    }

    /**
     * Counts how many paths have a given node on them.
     * @param paths List of paths to check
     * @param nodeId Target node
     */
    public static long countPathsWithNodeAsWaypoint(ArrayList<ArrayList<Long>> paths, long nodeId) {
        int count = 0;
        for(ArrayList<Long> path : paths) {
            if(ShortestPathFinder.pathPassesTroughNode(path, nodeId))
                count++;
        }
        return count;
    }

    public ConcurrentHashMap<Long, DijkstraResult> getCache() { return cache; }
}
