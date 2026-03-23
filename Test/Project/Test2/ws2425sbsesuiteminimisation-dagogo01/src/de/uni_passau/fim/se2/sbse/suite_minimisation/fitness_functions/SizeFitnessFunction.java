package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * A fitness function that minimizes the size of the test suite.
 */
public class SizeFitnessFunction implements MinimizingFitnessFunction<TestSuiteChromosome> {

    private final int totalTestCases;

    /**
     * Constructs the size fitness function.
     *
     * @param totalTestCases the total number of test cases
     */
    public SizeFitnessFunction(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    /**
     * Calculates the size fitness value (f_size).
     *
     * @param chromosome the chromosome representing the test suite
     * @return the fitness value, normalized to [0, 1]
     */
    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        boolean[] genes = chromosome.getGenes();
        int selectedCount = 0;
        for (boolean gene : genes) {
            if (gene) {
                selectedCount++;
            }
        }
        return (double) selectedCount / totalTestCases;
    }
}
