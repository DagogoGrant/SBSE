package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TestOrder extends Encoding<TestOrder> {

    private final int[] positions;

    /**
     * Constructor for TestOrder with validation for uniqueness and value range.
     * 
     * @param mutation the mutation operation for the encoding
     * @param positions the order of test cases represented as an array
     * @throws IllegalArgumentException if positions are not valid or contain duplicates
     */
    public TestOrder(Mutation<TestOrder> mutation, int[] positions) {
        super(Objects.requireNonNull(mutation, "Mutation must not be null"));
        if (!isValid(positions)) {
            throw new IllegalArgumentException("Positions must be valid and unique.");
        }
        this.positions = positions.clone(); // Avoid reference issues by cloning
    }

    /**
     * Checks if the given positions are valid.
     * Positions must be unique and within the correct range.
     * 
     * @param tests the positions to be validated
     * @return true if the positions are valid, false otherwise
     */
    public static boolean isValid(final int[] tests) {
        Set<Integer> seen = new HashSet<>();
        for (int value : tests) {
            if (value < 0 || value >= tests.length || !seen.add(value)) {
                return false; // Duplicate or out of range values found
            }
        }
        return true;
    }

    /**
     * Creates a deep copy of the TestOrder instance.
     * 
     * @return a deep copy of the TestOrder instance
     */
    @Override
    public TestOrder deepCopy() {
        // Create a new instance of TestOrder with a deep copy of positions
        return new TestOrder(getMutation(), positions.clone());
    }

    /**
     * Returns the number of positions.
     * 
     * @return the size of the positions array
     */
    public int size() {
        return positions.length;
    }

    /**
     * Gets a copy of the positions array.
     * 
     * @return a clone of the positions array
     */
    public int[] getPositions() {
        return positions.clone(); // Clone to ensure the caller cannot modify internal data
    }

    @Override
    public TestOrder self() {
        return this;
    }
}
