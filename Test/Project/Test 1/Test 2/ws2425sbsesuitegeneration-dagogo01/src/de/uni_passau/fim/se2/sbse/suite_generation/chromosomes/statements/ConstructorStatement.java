package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ConstructorStatement implements Statement {

    private final Constructor<?> constructor;
    private Object[] parameters;
    private Object instance; // Holds the object created by the constructor

    public ConstructorStatement(Constructor<?> constructor, Object[] parameters) {
        this.constructor = constructor;
        this.parameters = parameters == null ? new Object[0] : parameters;
    }

    @Override
    public void run() {
        try {
            constructor.setAccessible(true);
            if (!validateParameters(parameters)) {
                throw new IllegalArgumentException("Parameters do not match constructor signature.");
            }
            instance = constructor.newInstance(parameters);
            System.out.println("Constructor executed. Instance created: " + instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke constructor: " + constructor.getName()
                    + ", with parameters: " + Arrays.toString(parameters), e);
        }
    }

    public Object getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Constructor has not been executed yet.");
        }
        return instance;
    }

    private boolean validateParameters(Object[] parameters) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length != parameters.length) return false;
        for (int i = 0; i < paramTypes.length; i++) {
            if (parameters[i] != null && !paramTypes[i].isAssignableFrom(parameters[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String params = Arrays.stream(parameters)
                .map(param -> param == null ? "null" : param.getClass().getSimpleName() + ": " + param)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return String.format("new %s(%s);",
                constructor.getDeclaringClass().getSimpleName(),
                params);
    }
}
