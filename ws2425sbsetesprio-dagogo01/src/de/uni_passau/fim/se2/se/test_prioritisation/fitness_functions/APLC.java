package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Arrays;
import java.util.Locale;


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
        if (coverageMatrix == null || coverageMatrix.length == 0 || coverageMatrix[0].length == 0) {
            throw new IllegalArgumentException("Coverage matrix cannot be null or empty");
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
            throw new NullPointerException("TestOrder cannot be null");
        }

        int[] order = testOrder.getPositions(); // Ordered indices of the test cases
        int n = order.length; // Number of test cases
        int m = coverageMatrix[0].length; // Number of lines

        // Array to track the first test case that covers each line
        int[] firstCoveringTests = new int[m];
        Arrays.fill(firstCoveringTests, Integer.MAX_VALUE);

        // Populate the firstCoveringTests array
        for (int testPosition = 0; testPosition < n; testPosition++) {
            int testCase = order[testPosition];
            for (int line = 0; line < m; line++) {
                if (coverageMatrix[testCase][line]) {
                    firstCoveringTests[line] = Math.min(firstCoveringTests[line], testPosition + 1); // Positions start at 1
                }
            }
        }

        // Compute the sum of the first covering test indices
        int sumOfFirstCoveringTests = Arrays.stream(firstCoveringTests).sum();

        double aplcValue = 1.0 - (1.0 / (n * m)) * sumOfFirstCoveringTests + (1.0 / (2 * n));

        // Format the result to 2 decimal places
        String formattedResult = String.format(Locale.US, "%.2f", aplcValue);

        // Return the formatted result as a double
        return Double.parseDouble(formattedResult);
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
        // For minimisation, negate the APLC value
        double aplcValue = applyAsDouble(encoding);
        // To minimise, simply invert the scale. APLC ranges [0, 1], so return 1 - aplcValue
        return 1.0 - aplcValue;
    }
}