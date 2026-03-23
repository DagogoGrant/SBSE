package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a constructor call as a test case statement.
 */
public class ConstructorStatement implements Statement {

    private final Constructor<?> constructor;
    private final Object[] parameters;

    /**
     * Creates a new constructor statement.
     *
     * @param constructor the constructor to invoke
     * @param parameters  the parameters for the constructor
     */
    public ConstructorStatement(Constructor<?> constructor, Object[] parameters) {
        this.constructor = constructor;
        this.parameters = parameters;
    }

    /**
     * Executes the constructor using Java reflection.
     *
     * @return the object created by the constructor
     */
    @Override
    public void run() {
        try {
            constructor.setAccessible(true);
            constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke constructor: " + constructor, e);
        }
    }

    /**
     * Returns the string representation of this constructor call as valid Java code.
     *
     * @return the Java code for this constructor call
     */
    @Override
    public String toString() {
        String params = Arrays.stream(parameters)
                .map(param -> param == null ? "null" : param.toString())
                .collect(Collectors.joining(", "));
        return String.format("new %s(%s);", constructor.getDeclaringClass().getSimpleName(), params);
    }
}
