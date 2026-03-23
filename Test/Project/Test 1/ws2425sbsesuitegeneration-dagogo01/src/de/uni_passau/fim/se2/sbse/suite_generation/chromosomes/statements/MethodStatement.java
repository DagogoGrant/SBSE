package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a method invocation as a test case statement.
 */
public class MethodStatement implements Statement {

    private final Method method;
    private final Object target;
    private final Object[] parameters;

    /**
     * Creates a new method statement.
     *
     * @param method     the method to invoke
     * @param target     the target object (instance) for the method
     * @param parameters the parameters for the method
     */
    public MethodStatement(Method method, Object target, Object[] parameters) {
        this.method = method;
        this.target = target;
        this.parameters = parameters;
    }

    /**
     * Executes the method using Java reflection.
     */
    @Override
    public void run() {
        try {
            method.setAccessible(true);
            method.invoke(target, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke method: " + method, e);
        }
    }

    /**
     * Returns the string representation of this method call as valid Java code.
     *
     * @return the Java code for this method call
     */
    @Override
    public String toString() {
        String params = Arrays.stream(parameters)
                .map(param -> param == null ? "null" : param.toString())
                .collect(Collectors.joining(", "));
        return String.format("%s.%s(%s);", 
                target == null ? "<static>" : target.getClass().getSimpleName(),
                method.getName(), params);
    }
}
