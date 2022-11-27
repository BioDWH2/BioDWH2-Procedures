package de.unibi.agbi.biodwh2.procedures.factory;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.procedures.model.GraphMode;
import de.unibi.agbi.biodwh2.procedures.utils.ShortestPathFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Associates each graph with its own unique ShortestPathFinder in order to cache results.
 */
public final class ShortestPathFinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortestPathFinderFactory.class);

    private static ConcurrentHashMap<BaseGraph, ShortestPathFinder> store = new ConcurrentHashMap<>();
    private static  ShortestPathFinderFactory instance;

    /**
     * @return Returns a new singleton instance
     */
    public static synchronized ShortestPathFinderFactory getInstance() {
        if(instance == null) {
            instance = new ShortestPathFinderFactory();
            LOGGER.info("Initializing new ShortestPathFinderFactory");
        }
        return instance;
    }

    /**
     * Attempts to fetch a ShortestPathFinder for a given graph and mode. If no such object is
     * present, a new ShortestPathFinder is created and associated with the graph in the lookup storage.
     * @param graph The graph that is used for analysis/computation
     * @param mode Active graph mode
     * @return ShortestPathFinder associated with the graph (either retrieved from storage or created)
     */
    public static ShortestPathFinder get(BaseGraph graph, GraphMode mode) {
        synchronized (store) {
            ShortestPathFinder shortestPathFinder = store.get(graph);
            // comparison by graph mode and graph object equality
            if(shortestPathFinder == null || shortestPathFinder.getMode() != mode) {
                LOGGER.info("No SPF for this graph, adding new entry");
                shortestPathFinder = new ShortestPathFinder(graph);
                store.put(graph, shortestPathFinder);
            }
            return shortestPathFinder;
        }
    }

}
