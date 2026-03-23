package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

public class CoverageFitnessFunction implements MaximizingFitnessFunction<TestSuiteChromosome> {
    private final boolean[][] coverageMatrix;

    public CoverageFitnessFunction(boolean[][] coverageMatrix) {
        if (coverageMatrix == null || coverageMatrix.length == 0) {
            throw new IllegalArgumentException("Coverage matrix cannot be null or empty.");
        }
        this.coverageMatrix = coverageMatrix;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome == null || chromosome.getTestCases() == null || chromosome.getTestCases().isEmpty()) {
            System.out.println("Empty chromosome detected. Returning fitness value 0.0.");
            return 0.0; // No coverage for empty chromosome
        }
    
        boolean[] coverage = new boolean[coverageMatrix[0].length];
    
        for (int testCase : chromosome.getTestCases()) {
            // Validate test case index
            if (testCase < 0 || testCase >= coverageMatrix.length) {
                System.err.println("Invalid test case index: " + testCase + " (CoverageMatrix length: " + coverageMatrix.length + ")");
                throw new IllegalArgumentException("Test case index out of bounds: " + testCase);
            }
    
            for (int line = 0; line < coverageMatrix[testCase].length; line++) {
                if (coverageMatrix[testCase][line]) {
                    coverage[line] = true;
                }
            }
        }
    
        long coveredLines = 0;
        for (boolean isCovered : coverage) {
            if (isCovered) {
                coveredLines++;
            }
        }
    
        return (double) coveredLines / coverage.length; // Normalized [0.0, 1.0]
    }
    

}
