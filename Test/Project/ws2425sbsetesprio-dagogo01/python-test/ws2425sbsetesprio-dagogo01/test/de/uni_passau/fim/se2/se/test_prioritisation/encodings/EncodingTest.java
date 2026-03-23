package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodingTest {

    @Test
    void testMutate() {
        // Create a dummy mutation for testing
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Updated constructor to include the mutation
        int[] positions = {0, 1, 2, 3, 4};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Mutate and verify the mutation
        TestOrder mutatedOrder = testOrder.mutate();
        assertNotNull(mutatedOrder);
        assertArrayEquals(positions, mutatedOrder.getPositions());
    }

    @Test
    void testDeepCopy() {
        // Create a dummy mutation for testing
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Updated constructor to include the mutation
        int[] positions = {0, 1, 2, 3, 4};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Deep copy and verify
        TestOrder copiedOrder = testOrder.deepCopy();
        assertNotSame(testOrder, copiedOrder);
        assertArrayEquals(testOrder.getPositions(), copiedOrder.getPositions());
    }
}
