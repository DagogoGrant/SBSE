package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

/**
 * A fitness function that maximizes the code coverage of the test suite.
 */
public class CoverageFitnessFunction implements MaximizingFitnessFunction<TestSuiteChromosome> {

    private final boolean[][] coverageMatrix;

    /**
     * Constructs the coverage fitness function.
     *
     * @param coverageMatrix a boolean matrix where matrix[i][j] indicates if test case i covers line j
     */
    public CoverageFitnessFunction(boolean[][] coverageMatrix) {
        this.coverageMatrix = coverageMatrix;
    }

    /**
     * Calculates the coverage fitness value (f_coverage).
     *
     * @param chromosome the chromosome representing the test suite
     * @return the fitness value, normalized to [0, 1]
     */
    @Override
    public double applyAsDouble(TestSuiteChromosome chromosome) {
        boolean[] genes = chromosome.getGenes();
        int totalLines = coverageMatrix[0].length;
        boolean[] coveredLines = new boolean[totalLines];

        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                for (int j = 0; j < totalLines; j++) {
                    coveredLines[j] |= coverageMatrix[i][j];
                }
            }
        }

        int coveredCount = 0;
        for (boolean covered : coveredLines) {
            if (covered) {
                coveredCount++;
            }
        }
        return (double) coveredCount / totalLines;
    }
}
