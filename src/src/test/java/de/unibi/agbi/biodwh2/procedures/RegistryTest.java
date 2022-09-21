package de.unibi.agbi.biodwh2.procedures;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistryTest {
    @SuppressWarnings("unused")
    public static class TestRegistryContainer implements RegistryContainer {
        @Procedure(name = "registry.test.testProcedure", description = "A test procedure")
        public static ResultSet testProcedure(final BaseGraph graph) {
            return null;
        }

        @Procedure(name = "registry.test.testArguments")
        public static ResultSet testArguments(final BaseGraph graph, final boolean testBool1, final Boolean testBool2,
                                              final byte testByte1, final Byte testByte2, final short testShort1,
                                              final Short testShort2, final int testInt1, final Integer testInt2,
                                              final long testLong1, final Long testLong2, final float testFloat1,
                                              final Float testFloat2, final double testDouble1,
                                              final Double testDouble2, final String testString1,
                                              final CharSequence testString2, final Node testNode,
                                              final Edge testEdge) {
            return null;
        }
    }

    @Test
    void testProcedureExists() {
        final Registry.ProcedureDefinition definition = Registry.getInstance().getProcedure(
                "registry.test.testProcedure");
        assertNotNull(definition);
        assertEquals("registry.test.testProcedure", definition.name);
        assertEquals("A test procedure", definition.description);
    }

    @Test
    void testArguments() {
        final Registry.ProcedureDefinition definition = Registry.getInstance().getProcedure(
                "registry.test.testArguments");
        assertNotNull(definition);
        assertEquals(18, definition.argumentNames.length);
        assertArrayEquals(new String[]{
                "testBool1", "testBool2", "testByte1", "testByte2", "testShort1", "testShort2", "testInt1", "testInt2",
                "testLong1", "testLong2", "testFloat1", "testFloat2", "testDouble1", "testDouble2", "testString1",
                "testString2", "testNode", "testEdge"
        }, definition.argumentNames);
        assertArrayEquals(new Registry.ArgumentType[]{
                Registry.ArgumentType.Bool, Registry.ArgumentType.Bool, Registry.ArgumentType.Int,
                Registry.ArgumentType.Int, Registry.ArgumentType.Int, Registry.ArgumentType.Int,
                Registry.ArgumentType.Int, Registry.ArgumentType.Int, Registry.ArgumentType.Int,
                Registry.ArgumentType.Int, Registry.ArgumentType.Float, Registry.ArgumentType.Float,
                Registry.ArgumentType.Float, Registry.ArgumentType.Float, Registry.ArgumentType.String,
                Registry.ArgumentType.String, Registry.ArgumentType.Node, Registry.ArgumentType.Edge
        }, definition.argumentTypes);
    }
}