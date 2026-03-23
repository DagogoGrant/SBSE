package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

/**
 * APLC implementation.
 */
public class APLC implements FitnessFunction<TestOrder> {

    private final boolean[][] coverageMatrix;

    /**
     * Constructs an APLC fitness function with the specified coverage matrix.
     *
     * @param coverageMatrix the coverage matrix to use
     */
    public APLC(boolean[][] coverageMatrix) {
        if (coverageMatrix == null || coverageMatrix.length == 0) {
            throw new IllegalArgumentException("Coverage matrix must not be null or empty.");
        }
        this.coverageMatrix = coverageMatrix.clone();
    }

    @Override
    public double applyAsDouble(TestOrder encoding) {
        int[] order = encoding.getPositions();
        int totalLines = coverageMatrix[0].length;
        int totalTestCases = order.length;

        // Tracks the first appearance of a line being covered
        int[] firstCoveragePosition = new int[totalLines];
        for (int i = 0; i < totalLines; i++) {
            firstCoveragePosition[i] = -1; // -1 indicates the line is not covered
        }

        // Calculate first coverage positions
        for (int i = 0; i < totalTestCases; i++) {
            int testIndex = order[i];
            for (int line = 0; line < totalLines; line++) {
                if (coverageMatrix[testIndex][line] && firstCoveragePosition[line] == -1) {
                    firstCoveragePosition[line] = i + 1; // 1-based index
                }
            }
        }

        // Compute APLC based on the formal formula
        double sum = 0.0;
        for (int pos : firstCoveragePosition) {
            if (pos != -1) {
                sum += pos;
            }
        }

        return 1.0 - (sum / (totalTestCases * totalLines)) + (0.5 / totalTestCases);
    }

    @Override
    public double maximise(TestOrder encoding) {
        return applyAsDouble(encoding);
    }

    @Override
    public double minimise(TestOrder encoding) {
        return -applyAsDouble(encoding);
    }
}
