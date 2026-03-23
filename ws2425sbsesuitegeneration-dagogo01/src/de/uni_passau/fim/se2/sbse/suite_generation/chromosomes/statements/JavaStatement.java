package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents different types of statements in a test case, including
 * initialization of the Class Under Test (CUT), field assignments, and method invocations.
 */
public abstract class JavaStatement implements Statement {

    protected Object cutInstance;

    /**
     * Constructor for initializing the CUT with a non-private constructor.
     * @param cutInstance The instance of the Class Under Test.
     */
    public JavaStatement(Object cutInstance) {
        this.cutInstance = cutInstance;
    }

    @Override
    public abstract void run();

    @Override
    public abstract String toString();

    /**
     * Represents a constructor call as a test case statement.
     */
    public static class ConstructorStatement extends JavaStatement {

        private final Constructor<?> constructor;
        private final Object[] parameters;

        public ConstructorStatement(Object cutInstance, Constructor<?> constructor, Object[] parameters) {
            super(cutInstance);
            this.constructor = constructor;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                constructor.setAccessible(true);
                cutInstance = constructor.newInstance(parameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke constructor: " + constructor, e);
            }
        }

        @Override
        public String toString() {
            String params = Arrays.stream(parameters)
                    .map(param -> param == null ? "null" : param.toString())
                    .collect(Collectors.joining(", "));
            return String.format("new %s(%s);", constructor.getDeclaringClass().getSimpleName(), params);
        }
    }

    /**
     * Represents a field assignment as a test case statement.
     */
    public static class FieldAssignmentStatement extends JavaStatement {

        private final Field field;
        private final Object value;

        public FieldAssignmentStatement(Object cutInstance, Field field, Object value) {
            super(cutInstance);
            this.field = field;
            this.value = value;
        }

        @Override
        public void run() {
            try {
                field.setAccessible(true);
                field.set(cutInstance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to assign value to field: " + field, e);
            }
        }

        @Override
        public String toString() {
            String valueStr = (value == null) ? "null" : value.toString();
            return String.format("%s.%s = %s;",
                    cutInstance == null ? field.getDeclaringClass().getSimpleName() : cutInstance.getClass().getSimpleName(),
                    field.getName(),
                    valueStr);
        }
    }

    /**
     * Represents a method invocation as a test case statement.
     */
    public static class MethodStatement extends JavaStatement {

        private final Method method;
        private final Object[] parameters;

        public MethodStatement(Object cutInstance, Method method, Object[] parameters) {
            super(cutInstance);
            this.method = method;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                method.setAccessible(true);
                method.invoke(cutInstance, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke method: " + method, e);
            }
        }

        @Override
        public String toString() {
            String params = Arrays.stream(parameters)
                    .map(param -> param == null ? "null" : param.toString())
                    .collect(Collectors.joining(", "));
            return String.format("%s.%s(%s);",
                    cutInstance == null ? "<static>" : cutInstance.getClass().getSimpleName(),
                    method.getName(), params);
        }
    }
}
