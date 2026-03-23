package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

import java.util.Objects;

public class TestSuiteSizeFitnessFunction<C extends TestSuiteChromosome> implements MinimizingFitnessFunction<C> {

    private final int totalTestCases;

    /**
     * Constructs a fitness function to measure the size of the test suite.
     *
     * @param totalTestCases The total number of test cases available.
     * @throws IllegalArgumentException if totalTestCases is non-positive.
     */
    public TestSuiteSizeFitnessFunction(int totalTestCases) {
        if (totalTestCases <= 0) {
            throw new IllegalArgumentException("Total number of test cases must be greater than 0");
        }
        this.totalTestCases = totalTestCases;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        boolean[] genes = chromosome.getGenes();
        int activeTests = 0;
        for (boolean gene : genes) {
            if (gene) activeTests++;
        }
        // Normalize active tests by the total number of test cases
        return (double) activeTests / totalTestCases; // Ensure result is between 0 and 1
    }
    

    

    @Override
    public boolean isMinimizing() {
        return true; // This is a minimizing fitness function
    }
}
