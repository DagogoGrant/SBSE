package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Objects;
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
        this.random = Objects.requireNonNull(random, "Random must not be null");
    }

    /**
     * Shifts a test to the beginning of the sequence.
     *
     * @param encoding the test order to be mutated
     * @return the mutated test order
     */
    @Override
    public TestOrder apply(TestOrder encoding) {
        Objects.requireNonNull(encoding, "Encoding must not be null");

        int[] positions = encoding.getPositions();
        int size = positions.length;

        // If there's only one element or zero elements, return the original encoding
        if (size <= 1) {
            return new TestOrder(encoding.getMutation(), positions.clone());
        }

        // Select a random index that is not 0 to shift to the beginning
        int selectedIndex = random.nextInt(size - 1) + 1;

        // Shift the selected element to the beginning
        int[] mutatedPositions = new int[size];
        mutatedPositions[0] = positions[selectedIndex];

        int currentIndex = 1;
        for (int i = 0; i < size; i++) {
            if (i != selectedIndex) {
                mutatedPositions[currentIndex] = positions[i];
                currentIndex++;
            }
        }

        return new TestOrder(encoding.getMutation(), mutatedPositions);
    }
}
