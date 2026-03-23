package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ShiftToBeginningMutation.
 */
class ShiftToBeginningMutationTest {

    @Test
    void testApplyWithFixedSeed() {
        int[] positions = {2, 1, 0};
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        Random random = new Random(42); // Fixed seed ensures consistent results
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        TestOrder mutatedOrder = mutation.apply(testOrder);
        int[] mutatedPositions = mutatedOrder.getPositions();

        // Validate the first position matches the value at the expected index
        int expectedIndex = 2; // Determined by Random(42)
        assertEquals(positions[expectedIndex], mutatedPositions[0],
            "The first position should match the value at the randomly selected index.");

        // Validate that all values from the original array are present and in correct order
        Arrays.sort(positions);
        Arrays.sort(mutatedPositions);
        assertArrayEquals(positions, mutatedPositions,
            "The mutated array should contain all the original values.");
    }

    @Test
    void testApplyWithSingleElement() {
        int[] positions = {5};
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder mutatedOrder = mutation.apply(testOrder);

        assertArrayEquals(positions, mutatedOrder.getPositions(),
            "A single-element array should remain unchanged.");
    }

    @Test
    void testApplyWithEmptyArray() {
        int[] positions = {};
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Expect the constructor to throw an exception for an empty array
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TestOrder(positions, dummyMutation),
            "Expected IllegalArgumentException for empty positions array."
        );

        assertEquals("Positions array must not be null or empty.", exception.getMessage());
    }

    @Test
    void testRandomness() {
        int[] positions = {0, 1, 2, 3, 4};
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        boolean[] positionMoved = new boolean[positions.length];

        for (int i = 0; i < 1000; i++) {
            TestOrder mutatedOrder = mutation.apply(testOrder);
            positionMoved[mutatedOrder.getPositions()[0]] = true;
        }

        // Ensure all positions have been selected as the first position at least once
        for (int i = 0; i < positionMoved.length; i++) {
            assertTrue(positionMoved[i], "Position " + i + " was never moved to the beginning.");
        }
    }
}
