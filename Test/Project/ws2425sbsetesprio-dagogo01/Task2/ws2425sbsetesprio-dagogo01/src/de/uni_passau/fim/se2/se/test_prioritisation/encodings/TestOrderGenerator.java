package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation; // Import Mutation
import java.util.Random;
import java.util.stream.IntStream;

/**
 * A generator for random test case orderings of a regression test suite.
 */
public class TestOrderGenerator implements EncodingGenerator<TestOrder> {

    private final Random random;
    private final Mutation<TestOrder> mutation;
    private final int testCases;

    /**
     * Creates a new test order generator with the given mutation and number of test cases.
     *
     * @param random     the source of randomness
     * @param mutation   the elementary transformation that the generated orderings will use
     * @param testCases  the number of test cases in the ordering
     */
    public TestOrderGenerator(final Random random, final Mutation<TestOrder> mutation, final int testCases) {
        if (testCases <= 0) {
            throw new IllegalArgumentException("Number of test cases must be greater than zero.");
        }
        this.random = random;
        this.mutation = mutation;
        this.testCases = testCases;
    }

    /**
     * Creates and returns a random permutation of test cases.
     *
     * @return random test case ordering
     */
    @Override
    public TestOrder get() {
        int[] positions = IntStream.range(0, testCases).toArray();
        // Shuffle the positions array to make it more random
        for (int i = positions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = positions[i];
            positions[i] = positions[j];
            positions[j] = temp;
        }
        return new TestOrder(mutation, positions);
    }
}
