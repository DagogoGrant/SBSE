package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * Fitness function to minimize the size of the test suite.
 */
public class TestSuiteSizeFitnessFunction implements MinimizingFitnessFunction<TestSuiteChromosome> {

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome == null || chromosome.getTestCases().length == 0) {
            throw new IllegalArgumentException("Chromosome is null or has no test cases.");
        }

        int selectedTests = chromosome.getTestCases().length; // Use length for array
        int totalTests = TestSuiteChromosome.getTotalTestCases(); // Use static method for total
        return (double) selectedTests / totalTests; // Normalize to range [0, 1]
    }
}
