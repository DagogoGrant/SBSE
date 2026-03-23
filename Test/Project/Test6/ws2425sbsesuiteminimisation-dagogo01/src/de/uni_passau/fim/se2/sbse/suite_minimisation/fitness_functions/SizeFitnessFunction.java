package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

public class SizeFitnessFunction implements MinimizingFitnessFunction<TestSuiteChromosome> {

    private final int totalTestCases;

    public SizeFitnessFunction(int totalTestCases) {
        if (totalTestCases <= 0) {
            throw new IllegalArgumentException("Total test cases must be greater than zero.");
        }
        this.totalTestCases = totalTestCases;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome.getTestCases().size() > totalTestCases) {
            throw new IllegalArgumentException("Chromosome size exceeds total test cases.");
        }
    
        int size = chromosome.getTestCases().size();
    
        // Normalize size to range [0.0, 1.0]
        return (double) size / totalTestCases;
    }
    
}
