package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodStatement implements Statement {

    private final Method method;
    private final Object target;
    private final Object[] parameters;

    /**
     * Constructs a MethodStatement to invoke a method on a target object with the specified parameters.
     *
     * @param method     the method to be invoked
     * @param target     the target object on which the method is invoked
     * @param parameters the parameters to be passed to the method
     * @throws IllegalArgumentException if method or target is null
     */
    public MethodStatement(Method method, Object target, Object[] parameters) {
        if (method == null) {
            throw new IllegalArgumentException("Method cannot be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null.");
        }
        this.method = method;
        this.target = target;
        this.parameters = parameters != null ? parameters : new Object[0]; // Default to empty array if null
    }

    @Override
    public void run() {
        try {
            // Invoke the method on the target object
            method.setAccessible(true); // Ensure the method is accessible
            method.invoke(target, parameters);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to execute MethodStatement for method '" + method.getName() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        // Generate the Java code representation of the method invocation
        String targetName = target.getClass().getSimpleName();
        String parameterString = Arrays.stream(parameters)
            .map(param -> param == null ? "null" : param.toString())
            .reduce((param1, param2) -> param1 + ", " + param2)
            .orElse("");
        return targetName + "." + method.getName() + "(" + parameterString + ");";
    }

    /**
     * Returns the method being invoked.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Returns the target object on which the method is invoked.
     *
     * @return the target object
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Returns the parameters passed to the method.
     *
     * @return the parameters array
     */
    public Object[] getParameters() {
        return parameters.clone();
    }
}
