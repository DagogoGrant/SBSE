package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * Fitness function to minimize the size of the test suite.
 */
public class TestSuiteSizeFitnessFunction implements MinimizingFitnessFunction<TestSuiteChromosome> {

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        int selectedTests = chromosome.getTestCases().size();
        int totalTests = chromosome.getTotalTestCases();
        return (double) selectedTests / totalTests; // Normalized in [0, 1]
    }
}
