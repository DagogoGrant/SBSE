package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * A fitness function that evaluates the coverage of a test suite. It calculates a weighted
 * fitness value based on the number of lines covered by the test cases and ensures the value
 * is normalized and capped at 1.0.
 */
public class CoverageFitnessFunction implements MaximizingFitnessFunction<TestSuiteChromosome> {

    private final boolean[][] coverageMatrix;
    private final double weightCoefficient; // Coefficient for adjusting weighted coverage

    /**
     * Constructor for the coverage fitness function.
     *
     * @param coverageMatrix    The coverage matrix indicating which lines are covered by test cases.
     * @param weightCoefficient Coefficient for balancing weighted coverage (0.0 to 1.0).
     * @throws IllegalArgumentException if inputs are invalid
     */
    public CoverageFitnessFunction(boolean[][] coverageMatrix, double weightCoefficient) {
        if (coverageMatrix == null || coverageMatrix.length == 0 || coverageMatrix[0].length == 0) {
            throw new IllegalArgumentException("Coverage matrix must be non-empty and rectangular.");
        }
        for (boolean[] row : coverageMatrix) {
            if (row.length != coverageMatrix[0].length) {
                throw new IllegalArgumentException("Coverage matrix must be rectangular.");
            }
        }
        
        if (weightCoefficient < 0.0 || weightCoefficient > 1.0) {
            throw new IllegalArgumentException("Weight coefficient must be between 0.0 and 1.0.");
        }
        this.coverageMatrix = coverageMatrix;
        this.weightCoefficient = weightCoefficient;
    }

    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome == null || chromosome.getTestCases() == null) {
            throw new IllegalArgumentException("Chromosome and its test cases cannot be null.");
        }

        boolean[] testCases = chromosome.getTestCases();
        if (testCases.length != coverageMatrix.length) {
            throw new IllegalArgumentException("Chromosome size must match the coverage matrix.");
        }

        int totalLines = coverageMatrix[0].length;
        if (totalLines == 0) {
            throw new IllegalStateException("Coverage matrix must have at least one line.");
        }

        boolean[] coveredLines = new boolean[totalLines];
        double weightedCoverage = 0.0;

        // Compute weights for each test case
        double[] testCaseWeights = computeTestCaseWeights();

        // Count the number of lines covered
        int coveredCount = 0;
        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i]) {
                for (int j = 0; j < totalLines; j++) {
                    if (coverageMatrix[i][j] && !coveredLines[j]) {
                        coveredLines[j] = true;
                        coveredCount++;
                        weightedCoverage += testCaseWeights[i];
                    }
                }
            }
        }

        // Calculate normalized coverage and weight
        double normalizedCoverage = (double) coveredCount / totalLines;
        double normalizedWeight = weightedCoverage / totalLines;

        // Compute and cap the fitness at 1.0
        double fitness = Math.max(0.0, Math.min(1.0, normalizedCoverage + weightCoefficient * normalizedWeight));

        // Debugging logs for insights
        System.out.println("Debugging CoverageFitnessFunction:");
        System.out.println("- Total Test Cases: " + testCases.length);
        System.out.println("- Total Lines: " + totalLines);
        System.out.println("- Covered Lines: " + coveredCount);
        System.out.println("- Weighted Coverage: " + weightedCoverage);
        System.out.println("- Normalized Coverage: " + normalizedCoverage);
        System.out.println("- Normalized Weight: " + normalizedWeight);
        System.out.println("- Final Fitness (Capped at 1.0): " + fitness);

        return fitness;
    }

    @Override
    public boolean isMinimizing() {
        return false; // Coverage is a maximizing objective
    }

    /**
     * Computes weights for test cases based on their contribution to unique line coverage.
     * Test cases that cover more unique lines receive higher weights.
     *
     * @return an array of weights for each test case.
     */
    private double[] computeTestCaseWeights() {
        double[] weights = new double[coverageMatrix.length];
        for (int i = 0; i < coverageMatrix.length; i++) {
            int uniqueCoverage = 0;
            for (boolean isCovered : coverageMatrix[i]) {
                if (isCovered) {
                    uniqueCoverage++;
                }
            }
            weights[i] = uniqueCoverage > 0
                ? (double) uniqueCoverage / coverageMatrix[i].length
                : 0.0; // Avoid division by zero for empty test cases
        }
        return weights;
    }
    
}
