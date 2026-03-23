package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

/**
 * Generates random test chromosomes for the Class Under Test (CUT).
 * It ensures that the generated chromosomes are valid and adhere to the constraints of the CUT.
 */
public class TestChromosomeGenerator implements ChromosomeGenerator<TestChromosome> {

    private final Mutation<TestChromosome> mutation;
    private final Crossover<TestChromosome> crossover;
    private final Random random;
    private final Class<?> classUnderTest;

    /**
     * Constructs a TestChromosomeGenerator.
     *
     * @param random          the random number generator, not {@code null}
     * @param mutation        the mutation strategy for test chromosomes, not {@code null}
     * @param crossover       the crossover strategy for test chromosomes, not {@code null}
     * @param classUnderTest  the class under test, not {@code null}
     */
    public TestChromosomeGenerator(
        Random random,
        final Mutation<TestChromosome> mutation,
        final Crossover<TestChromosome> crossover,
        final Class<?> classUnderTest
    ) {
        if (random == null || mutation == null || crossover == null || classUnderTest == null) {
            throw new IllegalArgumentException("Invalid TestChromosomeGenerator arguments");
        }
        this.random = random;
        this.mutation = mutation;
        this.crossover = crossover;
        this.classUnderTest = classUnderTest;
    }

    /**
     * Generates a random test chromosome.
     *
     * @return a new TestChromosome instance
     */
    @Override
    public TestChromosome get() {
        List<JavaStatement> statements = new ArrayList<>();
        List<JavaStatement> allStatements = Utils.allStatements(classUnderTest);

        if (allStatements.isEmpty()) {
            throw new IllegalStateException("No statements available for the class under test.");
        }

        // Add the initialization statement
        statements.add(allStatements.get(0));

        // Generate a random number of additional statements
        int numberOfStatements = random.nextInt(1, Math.min(allStatements.size(), 50));

        for (int i = 1; i < numberOfStatements; i++) {
            int rand = random.nextInt(1, allStatements.size());
            statements.add(allStatements.get(rand));
        }

        @SuppressWarnings("unchecked")
        List<Statement> castStatements = (List<Statement>) (List<?>) statements;

        logDebug("Generated chromosome with " + castStatements.size() + " statements.");
        return new TestChromosome(mutation, crossover, castStatements);
    }

    private void logDebug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
