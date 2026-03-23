package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.ConstructorStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.MethodCallStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Random;

public class TestChromosomeGenerator implements ChromosomeGenerator<TestChromosome> {

    private final Mutation<TestChromosome> mutation;
    private final Crossover<TestChromosome> crossover;
    private final Class<?> cutClass; // Class Under Test (CUT)
    private final Random random;
    private final Class<?> testGenerationTarget; // Target class for test generation

    /**
     * Constructs a new TestChromosomeGenerator.
     *
     * @param mutation            the mutation operator for TestChromosome
     * @param crossover           the crossover operator for TestChromosome
     * @param cutClass            the Class Under Test (CUT)
     * @param random              the source of randomness
     * @param testGenerationTarget the class for which tests are being generated
     * @throws NullPointerException if any of the arguments are null
     */
    public TestChromosomeGenerator(
            Mutation<TestChromosome> mutation,
            Crossover<TestChromosome> crossover,
            Class<?> cutClass,
            Random random,
            Class<?> testGenerationTarget
    ) {
        this.mutation = Objects.requireNonNull(mutation, "Mutation cannot be null");
        this.crossover = Objects.requireNonNull(crossover, "Crossover cannot be null");
        this.cutClass = Objects.requireNonNull(cutClass, "Class Under Test (CUT) cannot be null");
        this.random = Objects.requireNonNull(random, "Random cannot be null");
        this.testGenerationTarget = Objects.requireNonNull(testGenerationTarget, "Class for test generation cannot be null");
    }

    public TestChromosome get() {
        TestChromosome chromosome = new TestChromosome(mutation, crossover);
    
        // Add a constructor call
        chromosome.addStatement(generateConstructorStatement());
    
        // Add up to 50 random statements
        int maxStatements = random.nextInt(50) + 1;
        ConstructorStatement constructor = (ConstructorStatement) chromosome.getStatements().get(0);
        for (int i = 0; i < maxStatements; i++) {
            chromosome.addStatement(generateRandomStatement(constructor.getInstance()));
        }
    
        System.out.println("Generated Test Chromosome: " + chromosome);
        return chromosome;
    }
    
    private ConstructorStatement generateConstructorStatement() {
        try {
            Constructor<?>[] constructors = cutClass.getConstructors();
            if (constructors.length == 0) {
                throw new RuntimeException("No public constructors available for " + cutClass.getName());
            }
            Constructor<?> constructor = constructors[random.nextInt(constructors.length)];
            Object[] parameters = generateRandomParameters(constructor.getParameterTypes());
            Object instance = constructor.newInstance(parameters); // Create instance
            ConstructorStatement constructorStatement = new ConstructorStatement(constructor, parameters);
            constructorStatement.setInstance(instance); // Set instance
            return constructorStatement;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate a constructor statement for " + cutClass.getName(), e);
        }
    }
    
    
    private Statement generateRandomStatement(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance is null in generateRandomStatement");
        }
        if (random.nextBoolean()) {
            return generateMethodCallStatement(instance);
        } else {
            return generateFieldAssignmentStatement(instance);
        }
    }
    

    private Statement generateMethodCallStatement(Object instance) {
        try {
            Method[] methods = cutClass.getDeclaredMethods();
            if (methods.length == 0) {
                throw new RuntimeException("No methods available for " + cutClass.getName());
            }
            Method method = methods[random.nextInt(methods.length)];
            method.setAccessible(true); // Allow private method access
            Object[] parameters = generateRandomParameters(method.getParameterTypes());
            return new MethodCallStatement(instance, method, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate a method call statement for " + cutClass.getName(), e);
        }
    }
    
    private Statement generateFieldAssignmentStatement(Object instance) {
        try {
            Field[] fields = cutClass.getDeclaredFields();
            if (fields.length == 0) {
                throw new RuntimeException("No fields available for " + cutClass.getName());
            }
            Field field = fields[random.nextInt(fields.length)];
            field.setAccessible(true); // Allow private field access
            Object value = generateRandomValue(field.getType());
            return new FieldAssignmentStatement(instance, field, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate a field assignment statement for " + cutClass.getName(), e);
        }
    }
    

    private Object[] generateRandomParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateRandomValue(parameterTypes[i]);
        }
        return parameters;
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
}
