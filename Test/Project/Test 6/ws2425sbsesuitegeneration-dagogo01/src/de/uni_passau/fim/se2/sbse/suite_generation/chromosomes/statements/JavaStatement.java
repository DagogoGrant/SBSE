package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents various types of Java statements in a test case, including initialization,
 * method calls, and field assignments for the Class Under Test (CUT).
 *
 * This class provides a framework for defining and executing Java statements using reflection.
 * It ensures compatibility with the constraints of the test case generation problem.
 */
public abstract class JavaStatement implements Statement {

    protected Object cutInstance;

    /**
     * Constructor for JavaStatement.
     *
     * @param cutInstance The instance of the Class Under Test (CUT).
     */
    public JavaStatement(Object cutInstance) {
        this.cutInstance = Objects.requireNonNull(cutInstance, "CUT instance cannot be null");
    }

    /**
     * Executes the Java statement on the CUT.
     */
    @Override
    public abstract void run();

    /**
     * Generates a string representation of the Java statement.
     *
     * @return String representation of the statement.
     */
    @Override
    public abstract String toString();

    /**
     * Represents the initialization of the CUT.
     */
    public static class InitializationStatement extends JavaStatement {

        private final String className;
        private final Object[] parameters;

        /**
         * Constructor for InitializationStatement.
         *
         * @param cutInstance The instance of the CUT.
         * @param className   The name of the class being initialized.
         * @param parameters  The parameters for the constructor.
         */
        public InitializationStatement(Object cutInstance, String className, Object... parameters) {
            super(cutInstance);
            this.className = Objects.requireNonNull(className, "Class name cannot be null");
            this.parameters = parameters;
        }

        @Override
        public void run() {
            // Initialization is handled during construction.
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("new ").append(className).append("(");
            for (int i = 0; i < parameters.length; i++) {
                sb.append(parameters[i] != null ? parameters[i].toString() : "null");
                if (i < parameters.length - 1) sb.append(", ");
            }
            sb.append(");");
            return sb.toString();
        }
    }

    /**
     * Represents a method call on the CUT.
     */
    public static class MethodCallStatement extends JavaStatement {

        private final Method method;
        private final Object[] parameters;

        /**
         * Constructor for MethodCallStatement.
         *
         * @param cutInstance The instance of the CUT.
         * @param method      The method being called.
         * @param parameters  The parameters for the method call.
         */
        public MethodCallStatement(Object cutInstance, Method method, Object... parameters) {
            super(cutInstance);
            this.method = Objects.requireNonNull(method, "Method cannot be null");
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                if (Modifier.isStatic(method.getModifiers())) {
                    method.invoke(null, parameters);
                } else {
                    method.invoke(cutInstance, parameters);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute method: " + method.getName(), e);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("cut.").append(method.getName()).append("(");
            for (int i = 0; i < parameters.length; i++) {
                sb.append(parameters[i] != null ? parameters[i].toString() : "null");
                if (i < parameters.length - 1) sb.append(", ");
            }
            sb.append(");");
            return sb.toString();
        }
    }

    /**
     * Represents a field assignment on the CUT.
     */
    public static class FieldAssignmentStatement extends JavaStatement {

        private final Field field;
        private final Object value;

        /**
         * Constructor for FieldAssignmentStatement.
         *
         * @param cutInstance The instance of the CUT.
         * @param field       The field to assign a value to.
         * @param value       The value to assign to the field.
         */
        public FieldAssignmentStatement(Object cutInstance, Field field, Object value) {
            super(cutInstance);
            this.field = Objects.requireNonNull(field, "Field cannot be null");
            this.value = value;
        }

        @Override
        public void run() {
            try {
                field.setAccessible(true);
                field.set(cutInstance, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to assign value to field: " + field.getName(), e);
            }
        }

        @Override
        public String toString() {
            return "cut." + field.getName() + " = " + (value != null ? value.toString() : "null") + ";";
        }
    }
}
