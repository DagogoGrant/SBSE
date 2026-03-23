package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorStatement implements Statement {

    private final Constructor<?> constructor;
    private final Object[] parameters;
    private Object instance;

    /**
     * Constructs a ConstructorStatement with the specified constructor and parameters.
     *
     * @param constructor the constructor to be invoked
     * @param parameters  the parameters for the constructor
     * @throws IllegalArgumentException if the constructor or parameters are null
     */
    public ConstructorStatement(Constructor<?> constructor, Object[] parameters) {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor cannot be null.");
        }
        this.constructor = constructor;
        this.parameters = parameters != null ? parameters : new Object[0];
    }

    @Override
    public void run() {
        try {
            // Instantiate the class using the constructor
            this.instance = constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to execute ConstructorStatement: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the created instance after execution.
     *
     * @return the created object, or null if the constructor has not been executed
     */
    public Object getInstance() {
        return this.instance;
    }

    @Override
    public String toString() {
        // Convert the constructor call to a readable Java code representation
        StringBuilder code = new StringBuilder("new ");
        code.append(constructor.getDeclaringClass().getSimpleName()).append("(");
        code.append(Arrays.toString(parameters).replaceAll("\\[|\\]", ""));
        code.append(");");
        return code.toString();
    }
}
