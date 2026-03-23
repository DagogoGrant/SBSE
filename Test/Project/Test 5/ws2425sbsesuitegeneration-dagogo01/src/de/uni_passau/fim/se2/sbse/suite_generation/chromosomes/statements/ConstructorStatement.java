package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;

/**
 * Represents a constructor call as a statement.
 */
public class ConstructorStatement implements Statement {
    private final Constructor<?> constructor;
    private final Object[] parameters;
    private Object instance; // Instance created by this constructor.

    public ConstructorStatement(Constructor<?> constructor, Object[] parameters) {
        this.constructor = constructor;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        try {
            instance = constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute constructor: " + constructor, e);
        }
    }
     // Add this method to set the instance
     public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constructor.getDeclaringClass().getSimpleName()).append(" obj = new ");
        sb.append(constructor.getDeclaringClass().getSimpleName()).append("(");
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i]);
            if (i < parameters.length - 1) sb.append(", ");
        }
        sb.append(");");
        return sb.toString();
    }
    
}
