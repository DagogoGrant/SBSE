package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.Random;

/**
 * A generator for creating random TestSuiteChromosome instances using boolean-based representation.
 */
public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int totalTestCases; // Total number of test cases
    private final Mutation<TestSuiteChromosome> mutationOperator; // Mutation operator
    private final Crossover<TestSuiteChromosome> crossoverOperator; // Crossover operator
    private final Random random; // Random generator instance

    /**
     * Constructs a TestSuiteChromosomeGenerator with a provided Random instance.
     *
     * @param totalTestCases   Total number of test cases
     * @param mutationOperator Mutation operator for chromosomes
     * @param crossoverOperator Crossover operator for chromosomes
     * @param random           Random generator instance
     */
    public TestSuiteChromosomeGenerator(int totalTestCases,
                                        Mutation<TestSuiteChromosome> mutationOperator,
                                        Crossover<TestSuiteChromosome> crossoverOperator,
                                        Random random) {
        if (totalTestCases <= 0) {
            throw new IllegalArgumentException("Total test cases must be greater than 0.");
        }
        this.totalTestCases = totalTestCases;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.random = random;
    }

    /**
     * Constructs a TestSuiteChromosomeGenerator with the default Random instance.
     *
     * @param totalTestCases   Total number of test cases
     * @param mutationOperator Mutation operator for chromosomes
     * @param crossoverOperator Crossover operator for chromosomes
     */
    public TestSuiteChromosomeGenerator(int totalTestCases,
                                        Mutation<TestSuiteChromosome> mutationOperator,
                                        Crossover<TestSuiteChromosome> crossoverOperator) {
        this(totalTestCases, mutationOperator, crossoverOperator, new Random());
    }

    /**
     * Generates a random TestSuiteChromosome with valid test cases.
     *
     * @return a valid TestSuiteChromosome
     */
    @Override
    public TestSuiteChromosome get() {
        boolean[] testCases = new boolean[totalTestCases];

        // Randomly assign true or false to each test case
        for (int i = 0; i < totalTestCases; i++) {
            testCases[i] = random.nextBoolean();
        }

        // Ensure at least one test case is included
        if (!containsTrue(testCases)) {
            testCases[random.nextInt(totalTestCases)] = true; // Force at least one test case to be true
        }

        // Create and return a TestSuiteChromosome
        return new TestSuiteChromosome(testCases, mutationOperator, crossoverOperator);
    }

    /**
     * Checks if the boolean array contains at least one true value.
     *
     * @param array the boolean array
     * @return true if the array contains at least one true value; false otherwise
     */
    private boolean containsTrue(boolean[] array) {
        for (boolean value : array) {
            if (value) return true;
        }
        return false;
    }
}
