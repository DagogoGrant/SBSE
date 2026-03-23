package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A mutation that shifts a randomly chosen test to the beginning of the sequence.
 */
public class ShiftToBeginningMutation implements Mutation<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    public ShiftToBeginningMutation(final Random random) {
        if (random == null) {
            throw new NullPointerException("Random source cannot be null");
        }
        this.random = random;
    }

    /**
     * Shifts a randomly chosen test to the beginning of the sequence.
     *
     * @param encoding the test order to be mutated
     * @return the mutated test order
     */
    @Override
    public TestOrder apply(TestOrder encoding) {
        if (encoding == null) {
            throw new NullPointerException("Test order cannot be null");
        }

        int[] positions = encoding.getPositions();
        if (positions.length == 0) {
            throw new IllegalArgumentException("Positions array cannot be empty");
        }

        // Copy positions to modify without affecting original encoding
        List<Integer> positionList = new ArrayList<>();
        for (int position : positions) {
            positionList.add(position);
        }

        // Select a random element to shift to the beginning
        int randomIndex = random.nextInt(positions.length);
        int selectedElement = positionList.remove(randomIndex);
        positionList.add(0, selectedElement);

        // Create a new positions array
        int[] mutatedPositions = positionList.stream().mapToInt(i -> i).toArray();

        return new TestOrder(mutatedPositions);
    }
}
