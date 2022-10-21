package de.unibi.agbi.biodwh2.procedures.model;

import java.util.Objects;

/**
 * Represents a pair of IDs, e.g. of two graph nodes.
 */
public class IdPair {

    private long firstID;
    private long secondID;

    public IdPair(final long first, final long second) {
        this.firstID = first;
        this.secondID = second;
    }

    /**
     * Overrides basic equality: The pair (1,2) is the same as (2,1), since it contains the same
     * IDs (order does not matter in this case).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IdPair idPair = (IdPair) o;
        return (Objects.equals(this.firstID, idPair.firstID) && Objects.equals(this.secondID, (idPair.secondID)) ||
               (Objects.equals(this.secondID, (idPair.firstID)) && Objects.equals(this.firstID, idPair.secondID)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstID, secondID) + Objects.hash(secondID, firstID);
    }
}
