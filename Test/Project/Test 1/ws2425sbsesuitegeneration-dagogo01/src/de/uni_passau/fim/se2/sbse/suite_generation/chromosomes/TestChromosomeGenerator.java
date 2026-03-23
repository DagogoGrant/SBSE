package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.ConstructorStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.MethodStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random test chromosomes for a given Class Under Test (CUT).
 */
public class TestChromosomeGenerator implements ChromosomeGenerator<TestChromosome> {

    private final Class<?> cut; // Class Under Test (CUT)
    private final Mutation<TestChromosome> mutation;
    private final Crossover<TestChromosome> crossover;
    private final Random random;
    private static final int MAX_STATEMENTS = 50;

    /**
     * Constructs a new TestChromosomeGenerator.
     *
     * @param cut       the Class Under Test (CUT)
     * @param mutation  the mutation operator
     * @param crossover the crossover operator
     */
    public TestChromosomeGenerator(Class<?> cut, Mutation<TestChromosome> mutation, Crossover<TestChromosome> crossover) {
        this.cut = cut;
        this.mutation = mutation;
        this.crossover = crossover;
        this.random = new Random();
    }

    /**
     * Generates a random test chromosome.
     *
     * @return a random TestChromosome
     */
    @Override
    public TestChromosome get() {
        TestChromosome chromosome = new TestChromosome(mutation, crossover);

        // Add a constructor statement as the first statement
        chromosome.addStatement(generateConstructorStatement());

        // Add random method and field assignment statements
        int numStatements = random.nextInt(MAX_STATEMENTS - 1) + 1; // Ensure at least 1 statement
        for (int i = 0; i < numStatements; i++) {
            if (random.nextBoolean()) {
                chromosome.addStatement(generateMethodStatement(chromosome));
            } else {
                chromosome.addStatement(generateFieldAssignmentStatement(chromosome));
            }
        }

        return chromosome;
    }

    /**
     * Generates a random constructor statement for the CUT.
     *
     * @return a ConstructorStatement
     */
    private ConstructorStatement generateConstructorStatement() {
        Constructor<?>[] constructors = cut.getDeclaredConstructors();
        Constructor<?> constructor = constructors[random.nextInt(constructors.length)];
        Object[] parameters = generateParameters(constructor.getParameterTypes());
        return new ConstructorStatement(constructor, parameters);
    }

    /**
     * Generates a random method statement for the CUT.
     *
     * @param chromosome the chromosome being generated
     * @return a MethodStatement
     */
    private MethodStatement generateMethodStatement(TestChromosome chromosome) {
        Method[] methods = cut.getDeclaredMethods();
        Method method = methods[random.nextInt(methods.length)];
        Object target = chromosome.getStatements().get(0); // Use the first statement (Constructor) as the target
        Object[] parameters = generateParameters(method.getParameterTypes());
        return new MethodStatement(method, target, parameters);
    }

    /**
     * Generates a random field assignment statement for the CUT.
     *
     * @param chromosome the chromosome being generated
     * @return a FieldAssignmentStatement
     */
    private FieldAssignmentStatement generateFieldAssignmentStatement(TestChromosome chromosome) {
        Field[] fields = cut.getDeclaredFields();
        Field field = fields[random.nextInt(fields.length)];
        Object target = chromosome.getStatements().get(0); // Use the first statement (Constructor) as the target
        Object value = generateParameter(field.getType());
        return new FieldAssignmentStatement(field, target, value);
    }

    /**
     * Generates random parameters for the given parameter types.
     *
     * @param parameterTypes the types of the parameters to generate
     * @return an array of random parameter values
     */
    private Object[] generateParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateParameter(parameterTypes[i]);
        }
        return parameters;
    }

    /**
     * Generates a random value for the given type.
     *
     * @param type the type of the value to generate
     * @return a random value of the given type
     */
    private Object generateParameter(Class<?> type) {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return random.nextInt(2048) - 1024; // Random int in range [-1024, 1023]
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return random.nextDouble() * 2048 - 1024; // Random double in range [-1024.0, 1023.0]
        } else if (type.equals(String.class)) {
            int length = random.nextInt(10) + 1; // Random string length [1, 10]
            return random.ints(32, 127) // Printable ASCII range
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else {
            return null; // For unsupported types, return null
        }
    }
}
