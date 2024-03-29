package de.unibi.agbi.biodwh2.procedures.utils;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.procedures.model.DijkstraResult;
import de.unibi.agbi.biodwh2.procedures.model.DistancePair;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;

import java.sql.Array;
import java.util.*;

/**
 * Finds shortest paths in a graph
 * <p>
 * TODO:
 *  - create result containers instead of hash maps (distance mappings, hop nodes, ...)
 */
public class ShortestPathFinder {
    private final BaseGraph graph;
    private HashMap<Long, Long> shortestPaths;
    private GraphMode mode;
    private boolean useEdgeWeights;

    public ShortestPathFinder(final BaseGraph graph, final GraphMode mode, final boolean useEdgeWeights) {
        this.graph = graph;
        this.mode = mode;
        this.useEdgeWeights = useEdgeWeights;
        this.shortestPaths = new HashMap<>();
    }

    public ShortestPathFinder(final BaseGraph graph, final GraphMode mode) {
        this(graph, mode, false);
    }

    public ShortestPathFinder(final BaseGraph graph) {
        this(graph, GraphMode.UNDIRECTED, false);
    }

    /**
     * Computes the shortest path from a given source to a given target using Dijkstra's algorithm.
     * @param sourceNodeId The source node id
     * @param targetNodeId The target node id
     * @return Maps the path length from source to target
     */
    public DijkstraResult dijkstra(final long sourceNodeId, final long targetNodeId) {

        final Map<Long, Long> distances = new HashMap<>();
        final Map<Long, Long> predecessors = new HashMap<>();

        for (final Node node : graph.getNodes()) {
            if (node.getId() != sourceNodeId) {
                distances.put(node.getId(), Long.MAX_VALUE);
                predecessors.put(node.getId(), null);
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
                    predecessors.put(neighborId, currentId);
                    queue.add(new DistancePair(neighborId, distances.get(neighborId)));
                }
            }
        }

        // filter result map so it only contains the source-target-pair
        distances.keySet().removeIf(key -> !key.equals(targetNodeId));
        return new DijkstraResult(distances, constructPath(predecessors, targetNodeId));
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
        final Map<Long, Long> predecessors = new HashMap<>();

        for (final Node node : graph.getNodes()) {
            if (!Objects.equals(node.getId(), sourceNodeId)) {
                distances.put(node.getId(), Long.MAX_VALUE);
                predecessors.put(node.getId(), null);
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
                    predecessors.put(neighborId, currentNodeId);
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
        return new DijkstraResult(distances);
    }

    /**
     * Modified version of dijkstra's algorithm to find all possible shortest paths between a source node and a target node.
     * @param sourceNodeId Source node ID
     * @param targetNodeId Target node ID
     * @return A list consisting of multiple paths (= lists containing the IDs of all nodes on the path)
     */
    public ArrayList<ArrayList<Long>> findAllShortestPaths(final long sourceNodeId, final long targetNodeId) {

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

        // Construct list of possible paths from source to target
        ArrayList<ArrayList<Long>> paths = new ArrayList<>();
        constructNestedPaths(paths, targetNodeId, parents, new ArrayList<>());
        for(ArrayList<Long> path : paths) {
            Collections.reverse(path);
        }
        return paths;
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

    /**
     * Recursively constructs a path from a source node to a target node by ordering all predecessor nodes
     * for the target node.
     * @param predecessors Map holding all pairs node -> predecessor
     * @param targetNodeId ID of the target node (-> last node in path)
     * @return List containing all node ids in the correct order from source to target
     */
    private ArrayList<Long> constructPath(final Map<Long, Long> predecessors, final long targetNodeId) {
        ArrayList<Long> path = new ArrayList<>();
        path.add(targetNodeId);
        long nodeId = targetNodeId;
        while(predecessors.get(nodeId) != null) {
            nodeId = predecessors.get(nodeId);
            System.out.println(nodeId);
            path.add(0, nodeId);
        }
        return path;
    }

    private void constructNestedPaths(ArrayList<ArrayList<Long>> paths, long nodeId, final Map<Long, ArrayList<Long>> parents, ArrayList<Long> path) {

        if(parents.get(nodeId).contains( - 1L)) {
            paths.add(new ArrayList<>(path));
            return;
        }

        for(long id : parents.get(nodeId)) {
            path.add(nodeId);
            constructNestedPaths(paths, id, parents, path);
            path.remove(path.size() - 1);
        }

    }

}
