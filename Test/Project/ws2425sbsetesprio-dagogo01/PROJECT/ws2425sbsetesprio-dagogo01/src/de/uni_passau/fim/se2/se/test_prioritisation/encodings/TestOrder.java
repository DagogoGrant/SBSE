package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a solution encoding for test cases in the form of an ordered array.
 */
public class TestOrder extends Encoding<TestOrder> {

    private final int[] positions;

    /**
     * Creates a new test order with the given mutation and test case ordering.
     *
     * @param mutation  the mutation to be used with this encoding
     * @param positions the test case ordering
     * @throws IllegalArgumentException if the provided positions are not valid
     */
    public TestOrder(Mutation<TestOrder> mutation, int[] positions) {
        super(mutation);
        if (!isValid(positions)) {
            throw new IllegalArgumentException("Invalid test case ordering provided.");
        }
        this.positions = Arrays.copyOf(positions, positions.length);
    }

    /**
     * Tells whether the given array represents a valid regression test case prioritization encoding.
     * By convention, every test must have a unique identifier starting at 0. Since ranges are contiguous,
     * this implies that numbers must only occur once and be located in the range from 0 to n-1.
     *
     * @param tests the test suite prioritization array to check
     * @return {@code true} if the given prioritization is valid, {@code false} otherwise
     */
    public static boolean isValid(final int[] tests) {
        if (tests == null || tests.length == 0) {
            return false; // Empty or null array is invalid
        }
        int n = tests.length;

        // Check if all elements are in range [0, n-1] and unique
        Set<Integer> uniqueElements = new HashSet<>();
        for (int test : tests) {
            if (test < 0 || test >= n || !uniqueElements.add(test)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestOrder deepCopy() {
        return new TestOrder(getMutation(), positions);
    }

    /**
     * Returns the number of test cases in this test case ordering.
     *
     * @return the number of test cases
     */
    public int size() {
        return positions.length;
    }

    /**
     * Returns a reference to the underlying internal backing array.
     *
     * @return the orderings array
     */
    public int[] getPositions() {
        return Arrays.copyOf(positions, positions.length);
    }

    @Override
    public TestOrder self() {
        return this;
    }

    @Override
    public String toString() {
        return "TestOrder{" + "positions=" + Arrays.toString(positions) + '}';
    }
}
