package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * A generator for random test case orderings of a regression test suite. In the literature, indices
 * would start at 1. However, we let them start at 0 as this simplifies the implementation. The
 * highest index is given by the number of test cases minus 1. The range of indices is contiguous.
 */
public class TestOrderGenerator implements EncodingGenerator<TestOrder> {

    /**
     * The source of randomness for generating orderings.
     */
    private final Random random;

    /**
     * The mutation operator to be used with the generated test orders.
     */
    private final Mutation<TestOrder> mutation;

    /**
     * The number of test cases in the generated orderings.
     */
    private final int testCases;

    /**
     * Creates a new test order generator with the given mutation and number of test cases.
     *
     * @param random     the source of randomness
     * @param mutation   the elementary transformation that the generated orderings will use
     * @param testCases  the number of test cases in the ordering
     * @throws IllegalArgumentException if the number of test cases is less than 1
     * @throws NullPointerException if random or mutation is null
     */
    public TestOrderGenerator(final Random random, final Mutation<TestOrder> mutation, final int testCases) {
        if (testCases < 1) {
            throw new IllegalArgumentException("Number of test cases must be at least 1.");
        }
        if (random == null || mutation == null) {
            throw new NullPointerException("Random and mutation must not be null.");
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
        // Create a list of integers from 0 to testCases - 1
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < testCases; i++) {
            positions.add(i);
        }

        // Shuffle the list to create a random permutation
        Collections.shuffle(positions, random);

        // Convert the list to an array
        int[] ordering = positions.stream().mapToInt(Integer::intValue).toArray();

        // Return a new TestOrder using the generated permutation and the mutation operator
        return new TestOrder(mutation, ordering);
    }
}
