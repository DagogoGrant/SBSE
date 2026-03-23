package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Random;

/**
 * A mutation that shifts a test to the beginning of the sequence.
 */
public class ShiftToBeginningMutation implements Mutation<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    public ShiftToBeginningMutation(final Random random) {
        this.random = random;
    }

    /**
     * Shifts a test to the beginning of the sequence.
     *
     * @param encoding the test order to be mutated
     * @return the mutated test order
     */
    @Override
    public TestOrder apply(TestOrder encoding) {
        int[] positions = encoding.getPositions();
        if (positions.length <= 1) {
            return encoding; // No mutation needed for arrays of length 1 or less
        }
        int indexToShift = random.nextInt(positions.length);
        int valueToShift = positions[indexToShift];

        // Shift the selected value to the beginning of the array
        System.arraycopy(positions, 0, positions, 1, indexToShift);
        positions[0] = valueToShift;

        return new TestOrder(encoding.getMutation(), positions);
    }
}
