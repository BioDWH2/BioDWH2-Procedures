package de.unibi.agbi.biodwh2.procedures.utils;

import java.util.ArrayList;

/**
 * Utility class for network proximity calculations.
 */
public class ProximityUtils {

    /**
     * Threshold value for proximity:
     * Distance z-scores less or equal to this value indicate proximity, while larger values
     * indicate "farness"
     */
    public static float zThreshold = - 0.15f;

    /**
     * Calculates the z-score for a given proximity measure
     * @param distance Distance calculated according to a chosen prox. measure
     * @param meanReference Mean of the reference distribution
     * @param stdDevReference Standard deviation of the reference distribution
     * @return z-score for a given distance measure, enabled comparison to a reference distribution
     */
    public static float zScore(float distance, float meanReference, float stdDevReference) {
        return (distance - meanReference) / stdDevReference;
    }

    public static ArrayList<Float> generateReferenceDistribution() {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }
}
