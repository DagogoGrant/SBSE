package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Represents a field assignment as a test case statement.
 */
public class FieldAssignmentStatement implements Statement {

    private final Field field;
    private final Object target;
    private final Object value;

    /**
     * Creates a new field assignment statement.
     *
     * @param field  the field to assign to
     * @param target the target object (for instance fields, can be null for static fields)
     * @param value  the value to assign to the field
     */
    public FieldAssignmentStatement(Field field, Object target, Object value) {
        this.field = field;
        this.target = target;
        this.value = value;
    }

    /**
     * Executes the field assignment using Java reflection.
     */
    @Override
    public void run() {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to assign value to field: " + field, e);
        }
    }

    /**
     * Returns the string representation of this field assignment as valid Java code.
     *
     * @return the Java code for this field assignment
     */
    @Override
    public String toString() {
        String valueStr = (value == null) ? "null" : value.toString();
        return String.format("%s.%s = %s;",
                (target == null ? field.getDeclaringClass().getSimpleName() : target.getClass().getSimpleName()),
                field.getName(),
                valueStr);
    }
}
