package de.unibi.agbi.biodwh2.procedures;

import de.unibi.agbi.biodwh2.core.Factory;
import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
        for (final Class<RegistryContainer> containerClass : container)
            for (final Method method : containerClass.getDeclaredMethods())
                processContainerMethod(method);
        LOGGER.info("Registry:  " + container.size() + " containers, " + procedures.size() + " procedures, " +
                    functions.size() + " functions");
    }

    private void processContainerMethod(final Method method) {
        if (!Modifier.isStatic(method.getModifiers()))
            return;
        final Procedure[] procedureAnnotations = method.getAnnotationsByType(Procedure.class);
        if (procedureAnnotations != null)
            for (final Procedure annotation : procedureAnnotations)
                addProcedure(method, annotation);
        final Function[] functionAnnotations = method.getAnnotationsByType(Function.class);
        if (functionAnnotations != null)
            for (final Function annotation : functionAnnotations)
                addFunction(method, annotation);
    }

    private void addProcedure(final Method method, final Procedure annotation) {
        final ProcedureDefinition definition = new ProcedureDefinition(annotation.name(), annotation.description(),
                                                                       method);
        if (procedures.containsKey(definition.name))
            LOGGER.warn("Procedure with path '" + definition.name + "' already added, will be ignored.");
        else
            procedures.put(definition.name, definition);
    }

    private void addFunction(final Method method, final Function annotation) {
        final FunctionDefinition definition = new FunctionDefinition(annotation.name(), annotation.description(),
                                                                     method);
        if (functions.containsKey(definition.name))
            LOGGER.warn("Function with path '" + definition.name + "' already added, will be ignored.");
        else
            functions.put(definition.name, definition);
    }

    public static synchronized Registry getInstance() {
        if (instance == null)
            instance = new Registry();
        return instance;
    }

    public ResultSet callProcedure(final String name, final BaseGraph graph, Object... arguments) {
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

    public ResultSet callFunction(final String name, final BaseGraph graph, Object... arguments) {
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

    public ProcedureDefinition getProcedure(final String path) {
        return procedures.get(path);
    }

    public FunctionDefinition getFunction(final String path) {
        return functions.get(path);
    }

    private static abstract class MethodDefinition {
        public final String name;
        public final String signature;
        public final String description;
        public final String[] argumentNames;
        public final Class<?>[] argumentTypes;
        public final ArgumentType[] argumentSimpleTypes;
        final Method method;

        protected MethodDefinition(final String name, final String description, final Method method) {
            this.name = name;
            this.description = description;
            this.method = method;
            argumentNames = new String[method.getParameterCount() - 1];
            argumentTypes = new Class<?>[argumentNames.length];
            argumentSimpleTypes = new ArgumentType[argumentNames.length];
            final StringBuilder signature = new StringBuilder();
            for (int i = 0; i < argumentNames.length; i++) {
                final Parameter parameter = method.getParameters()[i + 1];
                argumentNames[i] = parameter.getName();
                argumentTypes[i] = parameter.getType();
                argumentSimpleTypes[i] = getType(parameter.getType());
                if (i > 0)
                    signature.append(", ");
                signature.append(argumentNames[i]).append(": ").append(argumentTypes[i]);
            }
            this.signature = signature.toString();
        }

        private ArgumentType getType(final Class<?> type) {
            if (type.equals(String.class) || type.equals(CharSequence.class))
                return ArgumentType.String;
            if (type.equals(Boolean.class) || type.equals(boolean.class))
                return ArgumentType.Bool;
            if (type.equals(Float.class) || type.equals(Double.class) || type.equals(float.class) || type.equals(
                    double.class)) {
                return ArgumentType.Float;
            }
            if (type.equals(Integer.class) || type.equals(Long.class) || type.equals(Short.class) || type.equals(
                    Byte.class) || type.equals(int.class) || type.equals(long.class) || type.equals(short.class) ||
                type.equals(byte.class)) {
                return ArgumentType.Int;
            }
            if (type.equals(Node.class))
                return ArgumentType.Node;
            if (type.equals(Edge.class))
                return ArgumentType.Edge;
            if (type.isEnum())
                return ArgumentType.Enum;
            return ArgumentType.Object;
        }
    }

    public static class ProcedureDefinition extends MethodDefinition {
        public ProcedureDefinition(final String name, final String description, final Method method) {
            super(name, description, method);
        }
    }

    public static class FunctionDefinition extends MethodDefinition {
        public FunctionDefinition(final String name, final String description, final Method method) {
            super(name, description, method);
        }
    }

    public enum ArgumentType {
        Bool,
        Int,
        Float,
        String,
        Node,
        Edge,
        Enum,
        Object
    }
}
