package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Method;

/**
 * Represents a method call as a statement.
 */
public class MethodCallStatement implements Statement {
    private final Object instance;
    private final Method method;
    private final Object[] parameters;

    public MethodCallStatement(Object instance, Method method, Object[] parameters) {
        this.instance = instance;
        this.method = method;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        try {
            method.invoke(instance, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute method: " + method.getName(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(instance.getClass().getSimpleName()).append(".");
        sb.append(method.getName()).append("(");
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i]);
            if (i < parameters.length - 1) sb.append(", ");
        }
        sb.append(");");
        return sb.toString();
    }
}
