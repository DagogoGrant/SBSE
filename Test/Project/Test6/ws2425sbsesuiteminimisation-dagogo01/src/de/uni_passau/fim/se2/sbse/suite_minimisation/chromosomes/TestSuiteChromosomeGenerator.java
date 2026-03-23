package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A generator for creating random TestSuiteChromosomes.
 */
public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int totalTestCases; // Total available test cases
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;

    /**
     * Constructs a new TestSuiteChromosomeGenerator.
     *
     * @param totalTestCases total number of available test cases
     * @param mutation       mutation operator
     * @param crossover      crossover operator
     */
    public TestSuiteChromosomeGenerator(int totalTestCases, 
                                         Mutation<TestSuiteChromosome> mutation, 
                                         Crossover<TestSuiteChromosome> crossover) {
        if (totalTestCases <= 0) {
            throw new IllegalArgumentException("Total test cases must be greater than 0.");
        }
        if (mutation == null || crossover == null) {
            throw new IllegalArgumentException("Mutation and crossover operators cannot be null.");
        }
        Objects.requireNonNull(mutation, "Mutation cannot be null");
    Objects.requireNonNull(crossover, "Crossover cannot be null");
    
    System.out.println("Initializing TestSuiteChromosomeGenerator");
    System.out.println("Total test cases: " + totalTestCases);
    System.out.println("Mutation: " + mutation);
    System.out.println("Crossover: " + crossover);

        this.totalTestCases = totalTestCases;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    @Override
    public TestSuiteChromosome get() {
        Random random = Randomness.random();
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, totalTestCases);
    
        List<Integer> testCases = new ArrayList<>();
        for (int i = 0; i < totalTestCases; i++) {
            if (random.nextDouble() < 0.5) { // 50% chance to include each test case
                if (i < 0 || i >= totalTestCases) {
                    System.err.println("Generated test case index out of bounds: " + i + " (totalTestCases: " + totalTestCases + ")");
                    throw new IllegalArgumentException("Generated test case index out of bounds: " + i);
                }
                testCases.add(i);
            }
        }
    
        if (testCases.isEmpty()) {
            int fallbackIndex = random.nextInt(totalTestCases);
            if (fallbackIndex < 0 || fallbackIndex >= totalTestCases) {
                System.err.println("Fallback test case index out of bounds: " + fallbackIndex + " (totalTestCases: " + totalTestCases + ")");
                throw new IllegalArgumentException("Fallback test case index out of bounds: " + fallbackIndex);
            }
            testCases.add(fallbackIndex);
        }
    
        chromosome.setTestCases(testCases);
        return chromosome;
    }
    
    
/**
 * Determines whether to include a test case using a bias.
 *
 * @param index        the index of the test case
 * @param total        the total number of test cases
 * @param random       the Random instance
 * @return true if the test case should be included, false otherwise
 */
private boolean biasedRandomInclude(int index, int total, Random random) {
    // Calculate a bias factor based on proximity to the middle
    double middle = total / 2.0;
    double distanceFromMiddle = Math.abs(index - middle);
    double biasFactor = 1.0 - (distanceFromMiddle / middle); // Higher for middle indices

    // Apply bias by scaling the random threshold
    double randomValue = random.nextDouble();
    return randomValue < biasFactor;
}

}
