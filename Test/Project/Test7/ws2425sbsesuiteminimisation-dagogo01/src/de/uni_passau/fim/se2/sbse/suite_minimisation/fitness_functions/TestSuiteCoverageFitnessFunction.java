package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import java.util.Objects;

public class TestSuiteCoverageFitnessFunction<C extends TestSuiteChromosome> implements MaximizingFitnessFunction<C> {

    private final boolean[][] coverageMatrix;

    public TestSuiteCoverageFitnessFunction(boolean[][] coverageMatrix) {
        Objects.requireNonNull(coverageMatrix, "Coverage matrix must not be null");

        if (coverageMatrix.length == 0) {
            throw new IllegalArgumentException("Coverage matrix must not be empty");
        }

        int rowLength = coverageMatrix[0].length;
        for (boolean[] row : coverageMatrix) {
            if (row.length != rowLength) {
                throw new IllegalArgumentException("All rows in the coverage matrix must have the same length");
            }
        }

        this.coverageMatrix = coverageMatrix;
    }
    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        if (chromosome == null) {
            throw new NullPointerException("Chromosome must not be null.");
        }
    
        boolean[] genes = chromosome.getGenes();
        if (genes.length != coverageMatrix.length) {
            throw new IllegalArgumentException("Chromosome gene length must match coverage matrix rows.");
        }
    
        int totalLines = coverageMatrix[0].length; // Total number of lines in the coverage matrix
        boolean[] coveredLines = new boolean[totalLines];
    
        // Calculate the covered lines based on active test cases
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) { // If the test case is active
                for (int j = 0; j < totalLines; j++) {
                    if (coverageMatrix[i][j]) {
                        coveredLines[j] = true;
                    }
                }
            }
        }
    
        // Count the unique covered lines
        int totalCoveredLines = 0;
        for (boolean covered : coveredLines) {
            if (covered) {
                totalCoveredLines++;
            }
        }
    
        // Normalize coverage based on the total number of lines
        double normalizedCoverage = (double) totalCoveredLines / totalLines;
    
        if (normalizedCoverage < 0 || normalizedCoverage > 1) {
            throw new IllegalArgumentException("Normalized coverage out of bounds: " + normalizedCoverage);
        }
    
        return normalizedCoverage;
    }
    
    
    @Override
    public boolean isMinimizing() {
        return false; // This is a maximizing fitness function
    }
}
