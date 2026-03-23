package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * Fitness function to maximize the achieved coverage of the test suite.
 */
public class TestSuiteCoverageFitnessFunction implements MaximizingFitnessFunction<TestSuiteChromosome> {

    private final boolean[][] coverageMatrix; // Coverage data

    public TestSuiteCoverageFitnessFunction(boolean[][] coverageMatrix) {
        this.coverageMatrix = coverageMatrix;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        return chromosome.getCoverageFitness(coverageMatrix); // Normalized in [0, 1]
    }
}

