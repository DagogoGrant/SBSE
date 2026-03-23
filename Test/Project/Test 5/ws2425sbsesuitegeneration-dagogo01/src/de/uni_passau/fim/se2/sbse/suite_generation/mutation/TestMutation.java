package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.ConstructorStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.MethodCallStatement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class TestMutation implements Mutation<TestChromosome> {

    private final Random random = new Random();

    @Override
    public TestChromosome apply(final TestChromosome parent) {
        // Create a copy of the parent chromosome to mutate
        TestChromosome offspring = parent.copy();

        // Decide the type of mutation to perform
        int mutationType = random.nextInt(3); // 0 = modify, 1 = add, 2 = remove

        switch (mutationType) {
            case 0 -> modifyStatement(offspring); // Modify an existing statement
            case 1 -> addStatement(offspring);    // Add a new statement
            case 2 -> removeStatement(offspring); // Remove an existing statement
        }

        return offspring;
    }

    private void modifyStatement(TestChromosome chromosome) {
        if (chromosome.size() > 0) {
            int index = random.nextInt(chromosome.size());
            Statement oldStatement = chromosome.getStatements().get(index);
            Statement newStatement = generateRandomStatement(
                    getInstanceFromConstructor(chromosome.getStatements().get(0))
            );
            chromosome.modifyStatement(index, newStatement);
        }
    }

    private void addStatement(TestChromosome chromosome) {
        if (chromosome.size() < 50) { // Enforce the maximum limit of 50 statements
            Statement newStatement = generateRandomStatement(
                    getInstanceFromConstructor(chromosome.getStatements().get(0))
            );
            chromosome.addStatement(newStatement);
        }
    }

    private void removeStatement(TestChromosome chromosome) {
        if (chromosome.size() > 1) { // Ensure at least one statement remains (constructor)
            int index = random.nextInt(chromosome.size() - 1) + 1; // Avoid removing constructor
            chromosome.removeStatement(index);
        }
    }

    private Statement generateRandomStatement(Object instance) {
        if (random.nextBoolean()) {
            return generateMethodCallStatement(instance);
        } else {
            return generateFieldAssignmentStatement(instance);
        }
    }

    private Statement generateMethodCallStatement(Object instance) {
        try {
            Method[] methods = instance.getClass().getDeclaredMethods();
            if (methods.length == 0) {
                throw new RuntimeException("No methods available for " + instance.getClass().getName());
            }
            Method method = methods[random.nextInt(methods.length)];
            Object[] parameters = generateRandomParameters(method.getParameterTypes());
            return new MethodCallStatement(instance, method, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate a method call statement.", e);
        }
    }

    private Statement generateFieldAssignmentStatement(Object instance) {
        try {
            Field[] fields = instance.getClass().getDeclaredFields();
            if (fields.length == 0) {
                throw new RuntimeException("No fields available for " + instance.getClass().getName());
            }
            Field field = fields[random.nextInt(fields.length)];
            Object value = generateRandomValue(field.getType());
            return new FieldAssignmentStatement(instance, field, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate a field assignment statement.", e);
        }
    }

    private Object generateRandomValue(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return random.nextInt(2048) - 1024;
        } else if (type == double.class || type == Double.class) {
            return random.nextDouble() * 2048 - 1024;
        } else if (type == String.class) {
            int length = random.nextInt(10) + 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append((char) (random.nextInt(95) + 32));
            }
            return sb.toString();
        } else if (type == boolean.class || type == Boolean.class) {
            return random.nextBoolean();
        } else if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants[random.nextInt(constants.length)];
        } else {
            return null;
        }
    }

    private Object[] generateRandomParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateRandomValue(parameterTypes[i]);
        }
        return parameters;
    }

    private Object getInstanceFromConstructor(Statement statement) {
        if (statement instanceof ConstructorStatement constructorStatement) {
            return constructorStatement.getInstance();
        }
        throw new IllegalArgumentException("First statement is not a ConstructorStatement.");
    }
}
