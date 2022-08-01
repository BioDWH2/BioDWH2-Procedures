package de.unibi.agbi.biodwh2.procedures.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.procedures.Procedure;
import de.unibi.agbi.biodwh2.procedures.RegistryContainer;
import de.unibi.agbi.biodwh2.procedures.ResultSet;

/**
 * Contains procedures for network proximity analysis.
 */
public final class GraphProximityProcedures implements RegistryContainer {

    @Procedure(name = "analysis.network.proximity.closest", signature = "TODO",
            description = "Calculates the Closest measure for a drug target set and a disease protein set")
    public static ResultSet closest(Graph targets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.shortest", signature = "TODO",
            description = "Calculates the Shortest measure for a drug target set and a disease protein set")
    public static ResultSet shortest(Graph targets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.kernel", signature = "TODO",
            description = "Calculates the Kernel measure for a drug target set and a disease protein set")
    public static ResultSet kernel(Graph targets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.centre", signature = "TODO",
            description = "Calculates the Centre measure for a drug target set and a disease protein set")
    public static ResultSet centre(Graph targets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

    @Procedure(name = "analysis.network.proximity.separation", signature = "TODO",
            description = "Calculates the Separation measure for a drug target set and a disease protein set")
    public static ResultSet separation(Graph targets, Graph diseaseProteins) {
        throw new UnsupportedOperationException(Thread.currentThread().getStackTrace()[1].getMethodName() +": Not yet implemented!");
    }

}
