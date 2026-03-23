package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OrderCrossoverTest {

    @Test
    void testOrderCrossover() {
        // Setup Random with a fixed seed to ensure consistency
        Random random = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        OrderCrossover crossover = new OrderCrossover(random);

        // Parent test orders
        TestOrder parent1 = new TestOrder(mutation, new int[]{0, 1, 2, 3});
        TestOrder parent2 = new TestOrder(mutation, new int[]{3, 2, 1, 0});

        // Apply crossover
        TestOrder child = crossover.apply(parent1, parent2);

        // Assertions
        assertNotNull(child);
        assertTrue(TestOrder.isValid(child.getPositions()), "The child should be a valid test order");
        assertEquals(4, child.size(), "The child should have the same size as parents");
        
        // Ensure child is a permutation of 0, 1, 2, 3
        boolean[] seen = new boolean[4];
        for (int pos : child.getPositions()) {
            assertTrue(pos >= 0 && pos < 4, "Position should be within the valid range");
            seen[pos] = true;
        }
        for (boolean value : seen) {
            assertTrue(value, "All positions should be present in the child");
        }
    }

    @Test
    void testOrderCrossoverIdenticalParents() {
        // Setup Random with a fixed seed to ensure consistency
        Random random = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        OrderCrossover crossover = new OrderCrossover(random);

        // Parent test orders (identical parents)
        TestOrder parent1 = new TestOrder(mutation, new int[]{0, 1, 2, 3});
        TestOrder parent2 = new TestOrder(mutation, new int[]{0, 1, 2, 3});

        // Apply crossover
        TestOrder child = crossover.apply(parent1, parent2);

        // Assertions
        assertNotNull(child);
        assertTrue(TestOrder.isValid(child.getPositions()), "The child should be a valid test order");
        assertArrayEquals(parent1.getPositions(), child.getPositions(), "Child should be identical to parents");
    }

    @Test
    void testOrderCrossoverDifferentLengthParents() {
        // Setup Random with a fixed seed to ensure consistency
        Random random = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        OrderCrossover crossover = new OrderCrossover(random);

        // Parent test orders with different lengths
        TestOrder parent1 = new TestOrder(mutation, new int[]{0, 1, 2, 3});
        TestOrder parent2 = new TestOrder(mutation, new int[]{0, 1, 2});

        // Expect an exception to be thrown
        assertThrows(IllegalArgumentException.class, () -> crossover.apply(parent1, parent2),
                "Parents with different lengths should throw an exception");
    }
}
