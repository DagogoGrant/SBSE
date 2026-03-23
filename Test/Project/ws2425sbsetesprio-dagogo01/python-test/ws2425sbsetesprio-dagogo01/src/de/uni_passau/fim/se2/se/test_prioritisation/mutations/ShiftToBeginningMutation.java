package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Random;

/**
 * Mutation implementation to shift a randomly selected position to the beginning.
 */
public class ShiftToBeginningMutation implements Mutation<TestOrder> {
    private final Random random;

    /**
     * Constructor for ShiftToBeginningMutation with a specified Random object.
     *
     * @param random Random object for selecting positions.
     */
    public ShiftToBeginningMutation(Random random) {
        this.random = random;
    }

    /**
     * Default constructor initializing with a new Random instance.
     */
    public ShiftToBeginningMutation() {
        this.random = new Random();
    }

    @Override
    public TestOrder apply(TestOrder testOrder) {
        int[] positions = testOrder.getPositions();
        if (positions.length <= 1) return testOrder; // No mutation needed for single-element arrays

        // Randomly select an index to move to the beginning
        int indexToMove = random.nextInt(positions.length);

        // Create a new array with the selected index moved to the beginning
        int[] mutatedPositions = new int[positions.length];
        mutatedPositions[0] = positions[indexToMove];

        int offset = 1;
        for (int i = 0; i < positions.length; i++) {
            if (i == indexToMove) continue; // Skip the moved element
            mutatedPositions[offset++] = positions[i];
        }

        // Return the mutated TestOrder
        return new TestOrder(mutatedPositions, testOrder.getMutation());
    }
}
