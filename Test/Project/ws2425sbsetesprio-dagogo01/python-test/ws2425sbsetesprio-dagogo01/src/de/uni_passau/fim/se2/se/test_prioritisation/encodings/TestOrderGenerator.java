package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

import java.util.Random;


/**
 * Generates instances of TestOrder encodings.
 */
public class TestOrderGenerator implements EncodingGenerator<TestOrder> {

    private final Random random;
    private final int numTestCases;
    private final Mutation<TestOrder> mutation;
    private final int[] fixedPositions;

    /**
     * Constructor for TestOrderGenerator with random mutation.
     *
     * @param random        The random number generator.
     * @param numTestCases  The number of test cases.
     * @param mutation      The mutation to apply to generated TestOrder instances.
     */
    public TestOrderGenerator(Random random, int numTestCases, Mutation<TestOrder> mutation) {
        this(random, numTestCases, mutation, null);
    }

    /**
     * Constructor for TestOrderGenerator with fixed positions.
     *
     * @param random        The random number generator.
     * @param numTestCases  The number of test cases.
     * @param mutation      The mutation to apply to generated TestOrder instances.
     * @param fixedPositions Fixed positions for the TestOrder (optional).
     */
    public TestOrderGenerator(Random random, int numTestCases, Mutation<TestOrder> mutation, int[] fixedPositions) {
        if (numTestCases <= 0) {
            throw new IllegalArgumentException("Number of test cases must be greater than 0.");
        }
        this.random = random;
        this.numTestCases = numTestCases;
        this.mutation = mutation != null ? mutation : new ShiftToBeginningMutation(random);
        this.fixedPositions = fixedPositions;
    }

    @Override
    public TestOrder generate() {
        int[] positions;

        // If fixed positions are provided, use them
        if (fixedPositions != null) {
            positions = fixedPositions.clone();
        } else {
            // Otherwise, generate and shuffle positions
            positions = new int[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                positions[i] = i;
            }
            shuffleArray(positions);
        }

        if (!isValid(positions)) {
            throw new IllegalStateException("Generated positions are invalid.");
        }

        return new TestOrder(positions, mutation);
    }

    /**
     * Shuffles an array using Fisher-Yates shuffle.
     *
     * @param array The array to shuffle.
     */
    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    /**
     * Validates if the given positions array is valid.
     *
     * @param positions The positions array.
     * @return True if valid, false otherwise.
     */
    private boolean isValid(int[] positions) {
        if (positions.length != numTestCases) return false;

        boolean[] seen = new boolean[numTestCases];
        for (int pos : positions) {
            if (pos < 0 || pos >= numTestCases || seen[pos]) {
                return false;
            }
            seen[pos] = true;
        }
        return true;
    }
}
