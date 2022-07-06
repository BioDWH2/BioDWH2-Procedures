package de.unibi.agbi.biodwh2.procedures;

import de.unibi.agbi.biodwh2.core.Factory;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Registry {
    private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);
    private static Registry instance;

    private final Map<String, ProcedureDefinition> procedures;
    private final Map<String, FunctionDefinition> functions;
    private final List<Class<RegistryContainer>> container;

    private Registry() {
        procedures = new HashMap<>();
        functions = new HashMap<>();
        container = Factory.getInstance().getImplementations(RegistryContainer.class);
        for (final Class<RegistryContainer> type : container) {
            for (final Method method : type.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())) {
                    final Procedure[] procedureAnnotations = method.getAnnotationsByType(Procedure.class);
                    if (procedureAnnotations != null) {
                        for (final Procedure procedureAnnotation : procedureAnnotations) {
                            final ProcedureDefinition definition = new ProcedureDefinition();
                            definition.method = method;
                            definition.name = procedureAnnotation.name();
                            definition.signature = procedureAnnotation.signature();
                            definition.description = procedureAnnotation.description();
                            if (procedures.containsKey(definition.name)) {
                                // TODO: throw
                            }
                            procedures.put(definition.name, definition);
                        }
                    }
                    final Function[] functionAnnotations = method.getAnnotationsByType(Function.class);
                    if (functionAnnotations != null) {
                        for (final Function functionAnnotation : functionAnnotations) {
                            final FunctionDefinition definition = new FunctionDefinition();
                            definition.method = method;
                            definition.name = functionAnnotation.name();
                            definition.signature = functionAnnotation.signature();
                            definition.description = functionAnnotation.description();
                            if (functions.containsKey(definition.name)) {
                                // TODO: throw
                            }
                            functions.put(definition.name, definition);
                        }
                    }
                }
            }
        }
        LOGGER.info("Registry:  " + container.size() + " containers, " + procedures.size() + " procedures, " +
                    functions.size() + " functions");
    }

    public static synchronized Registry getInstance() {
        if (instance == null)
            instance = new Registry();
        return instance;
    }

    public ResultSet callProcedure(final String name, final Graph graph, Object... arguments) {
        final ProcedureDefinition procedureDefinition = procedures.get(name);
        if (procedureDefinition != null) {
            try {
                final Object[] args = new Object[arguments.length + 1];
                args[0] = graph;
                System.arraycopy(arguments, 0, args, 1, arguments.length);
                return (ResultSet) procedureDefinition.method.invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                // TODO
                return null;
            }
        }
        // TODO: throw
        return null;
    }

    public ResultSet callFunction(final String name, final Graph graph, Object... arguments) {
        final FunctionDefinition functionDefinition = functions.get(name);
        if (functionDefinition != null) {
            try {
                final Object[] args = new Object[arguments.length + 1];
                args[0] = graph;
                System.arraycopy(arguments, 0, args, 1, arguments.length);
                return (ResultSet) functionDefinition.method.invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                // TODO
                return null;
            }
        }
        // TODO: throw
        return null;
    }

    public ProcedureDefinition[] getProcedures() {
        return procedures.values().toArray(new ProcedureDefinition[0]);
    }

    public FunctionDefinition[] getFunctions() {
        return functions.values().toArray(new FunctionDefinition[0]);
    }

    public static class ProcedureDefinition {
        public String name;
        public String signature;
        public String description;
        Method method;
    }

    public static class FunctionDefinition {
        public String name;
        public String signature;
        public String description;
        Method method;
    }
}
