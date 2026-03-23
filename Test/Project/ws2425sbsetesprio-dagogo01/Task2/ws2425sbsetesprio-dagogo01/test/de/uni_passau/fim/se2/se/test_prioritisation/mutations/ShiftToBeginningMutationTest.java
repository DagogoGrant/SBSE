package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShiftToBeginningMutationTest {

    @Test
    void testMutation() {
        Random random = new Random(42); // Use fixed seed for predictability
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        TestOrder testOrder = new TestOrder(mutation, new int[]{0, 1, 2, 3});
        TestOrder mutated = mutation.apply(testOrder);

        assertTrue(TestOrder.isValid(mutated.getPositions()));
        assertEquals(4, mutated.size());
        assertNotEquals(mutated.getPositions()[0], testOrder.getPositions()[0]);
    }
}
