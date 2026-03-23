package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Field;

/**
 * Represents a statement that assigns a value to a specific field of a target object.
 */
public class FieldAssignmentStatement implements Statement {

    private final Field field;
    private final Object target;
    private final Object value;

    /**
     * Constructs a FieldAssignmentStatement to assign a value to a field of a target object.
     *
     * @param field  the field to be assigned
     * @param target the target object whose field will be set
     * @param value  the value to assign to the field
     * @throws IllegalArgumentException if the field or target is null
     */
    public FieldAssignmentStatement(Field field, Object target, Object value) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null.");
        }
        this.field = field;
        this.target = target;
        this.value = value;
    }

    /**
     * Executes the field assignment by setting the value on the target object's field.
     */
    @Override
    public void run() {
        try {
            field.setAccessible(true); // Ensure we can access private/protected fields
            field.set(target, value); // Set the field value on the target object
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access the field during execution: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid argument passed to the field assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the string representation of this field assignment statement in Java code format.
     *
     * @return a string representation of the field assignment
     */
    @Override
    public String toString() {
        String targetName = target.getClass().getSimpleName();
        String valueRepresentation = (value == null) ? "null" : value.toString();
        return targetName + "." + field.getName() + " = " + valueRepresentation + ";";
    }

    /**
     * Gets the field being assigned.
     *
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * Gets the target object whose field is being assigned.
     *
     * @return the target object
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Gets the value being assigned to the field.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }
}
