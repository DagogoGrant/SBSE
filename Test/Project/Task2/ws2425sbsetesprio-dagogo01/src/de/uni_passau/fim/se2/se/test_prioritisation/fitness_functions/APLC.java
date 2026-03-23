package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

/**
 * The Average Percentage of Lines Covered (APLC) fitness function.
 */
public final class APLC implements FitnessFunction<TestOrder> {

    /**
     * The coverage matrix to be used when computing the APLC metric.
     */
    private final boolean[][] coverageMatrix;

    /**
     * Creates a new APLC fitness function with the given coverage matrix.
     *
     * @param coverageMatrix the coverage matrix to be used when computing the APLC metric
     */
    public APLC(final boolean[][] coverageMatrix) {
        if (coverageMatrix == null || coverageMatrix.length == 0) {
            throw new IllegalArgumentException("Coverage matrix must not be null or empty.");
        }
        this.coverageMatrix = coverageMatrix;
    }

    /**
     * Computes and returns the APLC for the given order of test cases.
     * Orderings that achieve a higher rate of coverage are rewarded with higher values.
     * The APLC ranges between 0.0 and 1.0.
     *
     * @param testOrder the proposed test order for which the fitness value will be computed
     * @return the APLC value of the given test order
     * @throws NullPointerException if {@code null} is given
     */
    @Override
    public double applyAsDouble(final TestOrder testOrder) throws NullPointerException {
        if (testOrder == null) {
            throw new NullPointerException("Test order must not be null.");
        }

        int[] order = testOrder.getPositions();
        int numTests = order.length;
        int numLines = coverageMatrix[0].length;

        int[] lineFirstCovered = new int[numLines];
        for (int i = 0; i < numLines; i++) {
            lineFirstCovered[i] = -1; // Initialize to indicate no coverage yet
        }

        for (int testIndex = 0; testIndex < numTests; testIndex++) {
            int testCase = order[testIndex];
            for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
                if (coverageMatrix[testCase][lineIndex] && lineFirstCovered[lineIndex] == -1) {
                    lineFirstCovered[lineIndex] = testIndex + 1;
                }
            }
        }

        double sum = 0.0;
        for (int firstCovered : lineFirstCovered) {
            if (firstCovered != -1) {
                sum += firstCovered;
            }
        }

        double aplc = 1.0 - ((sum / numLines) / numTests) + (1.0 / (2.0 * numTests));
        return Math.max(0.0, Math.min(1.0, aplc)); // Ensure value is between 0.0 and 1.0
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double maximise(TestOrder encoding) throws NullPointerException {
        return applyAsDouble(encoding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double minimise(TestOrder encoding) throws NullPointerException {
        return 1.0 - applyAsDouble(encoding);
    }
    
}
