package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestOrderTest {

    @Test
    void testConstructorAndGetPositions() {
        // Dummy mutation for testing
        Mutation<TestOrder> dummyMutation = order -> order;

        // Valid positions
        int[] positions = {0, 1, 2, 3};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Verify positions
        assertArrayEquals(positions, testOrder.getPositions(), "Positions should match the input.");
    }

    @Test
    void testConstructorThrowsExceptionForInvalidInput() {
        // Dummy mutation for testing
        Mutation<TestOrder> dummyMutation = order -> order;

        // Test for null positions
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(null, dummyMutation),
                "Constructor should throw an exception for null positions.");

        // Test for empty positions
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(new int[]{}, dummyMutation),
                "Constructor should throw an exception for empty positions.");
    }

    @Test
    void testDeepCopy() {
        // Dummy mutation for testing
        Mutation<TestOrder> dummyMutation = order -> order;

        // Valid positions
        int[] positions = {0, 1, 2, 3};
        TestOrder original = new TestOrder(positions, dummyMutation);
        TestOrder copy = original.deepCopy();

        // Verify deep copy
        assertNotSame(original, copy, "Deep copy should create a new instance.");
        assertArrayEquals(original.getPositions(), copy.getPositions(), "Positions should match in the deep copy.");
    }

    @Test
    void testSetAndGetFitness() {
        // Dummy mutation for testing
        Mutation<TestOrder> dummyMutation = order -> order;

        // Valid positions
        int[] positions = {0, 1, 2, 3};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Set fitness and verify
        testOrder.setFitness(0.95);
        assertEquals(0.95, testOrder.getFitness(), 0.0001, "Fitness value should match the set value.");
    }

    @Test
    void testSelfMethod() {
        // Dummy mutation for testing
        Mutation<TestOrder> dummyMutation = order -> order;

        // Valid positions
        int[] positions = {0, 1, 2, 3};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Verify self method
        assertSame(testOrder, testOrder.self(), "Self method should return the same instance.");
    }
}
