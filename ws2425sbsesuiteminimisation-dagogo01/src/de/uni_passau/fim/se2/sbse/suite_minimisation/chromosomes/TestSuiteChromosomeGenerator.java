package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import java.util.Random;
import java.util.logging.Logger;

public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int numTestCases;
    private final CoverageTracker coverageTracker;
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;
    private final Random random; // Allow injection for testability
    private static final Logger LOGGER = Logger.getLogger(TestSuiteChromosomeGenerator.class.getName());

    /**
     * Constructs a new TestSuiteChromosomeGenerator.
     *
     * @param numTestCases   the number of test cases (must be positive)
     * @param coverageTracker the coverage tracker (cannot be null)
     * @param mutation       the mutation operator (cannot be null)
     * @param crossover      the crossover operator (cannot be null)
     * @param random         the random generator (optional, defaults to new Random())
     */
    public TestSuiteChromosomeGenerator(int numTestCases,
                                        CoverageTracker coverageTracker,
                                        Mutation<TestSuiteChromosome> mutation,
                                        Crossover<TestSuiteChromosome> crossover,
                                        Random random) {
        if (numTestCases <= 0) {
            throw new IllegalArgumentException("Number of test cases must be positive.");
        }
        if (coverageTracker == null) {
            throw new IllegalArgumentException("CoverageTracker cannot be null");
        }
        if (mutation == null) {
            throw new IllegalArgumentException("Mutation operator cannot be null");
        }
        if (crossover == null) {
            throw new IllegalArgumentException("Crossover operator cannot be null");
        }

        this.numTestCases = numTestCases;
        this.coverageTracker = coverageTracker;
        this.mutation = mutation;
        this.crossover = crossover;
        this.random = (random != null) ? random : new Random();
    }

    @Override
    public TestSuiteChromosome get() {
        boolean[] testCases = new boolean[numTestCases];

        // Bias towards fewer test cases being selected (more 0s than 1s)
        double bias = 0.3; // Probability of setting a test case to true

        // Ensure at least one test case is selected
        boolean hasSelected = false;
        for (int i = 0; i < numTestCases; i++) {
            testCases[i] = random.nextDouble() < bias; // Use biased probability
            if (testCases[i]) {
                hasSelected = true;
            }
        }

        // If no test case is selected, force one random selection
        if (!hasSelected) {
            int forcedIndex = random.nextInt(numTestCases);
            testCases[forcedIndex] = true;

            // Debugging: Log the forced selection
            LOGGER.warning("No test case was initially selected. Forced selection at index: " + forcedIndex);
        }

        // Debugging: Log the final chromosome structure
        LOGGER.info("Generated chromosome: " + java.util.Arrays.toString(testCases));

        // Validate the chromosome
        if (testCases.length == 0) {
            throw new IllegalStateException("Generated chromosome has zero test cases.");
        }

        // Create and return a new TestSuiteChromosome
        return new TestSuiteChromosome(testCases, coverageTracker, mutation, crossover);
    }
}
