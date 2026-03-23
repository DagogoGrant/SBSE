package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Field;

/**
 * Represents a field assignment as a statement.
 */
public class FieldAssignmentStatement implements Statement {
    private final Object instance;
    private final Field field;
    private final Object value;

    public FieldAssignmentStatement(Object instance, Field field, Object value) {
        this.instance = instance;
        this.field = field;
        this.value = value;
    }

    @Override
    public void run() {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign value to field: " + field.getName(), e);
        }
    }

    @Override
    public String toString() {
        return instance.getClass().getSimpleName() + "." + field.getName() + " = " + value + ";";
    }
    
}
