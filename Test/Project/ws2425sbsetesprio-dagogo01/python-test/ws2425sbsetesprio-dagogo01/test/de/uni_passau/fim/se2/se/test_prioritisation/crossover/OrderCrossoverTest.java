package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderCrossoverTest {

    @Test
    void testApply() {
        // Dummy mutation for testing purposes
        Mutation<TestOrder> dummyMutation = order -> order;

        // Create parents
        TestOrder parent1 = new TestOrder(new int[]{0, 1, 2, 3}, dummyMutation);
        TestOrder parent2 = new TestOrder(new int[]{3, 2, 1, 0}, dummyMutation);

        // Initialize the OrderCrossover
        OrderCrossover crossover = new OrderCrossover();

        // Apply the crossover
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Validate offspring is not null
        assertNotNull(offspring, "Offspring should not be null");

        // Validate the offspring has the correct length
        assertEquals(parent1.getPositions().length, offspring.getPositions().length,
                "Offspring should have the same length as the parents");

        // Validate the offspring contains all positions without duplicates
        int[] offspringPositions = offspring.getPositions();
        Set<Integer> positionSet = new HashSet<>();
        for (int position : offspringPositions) {
            positionSet.add(position);
        }
        assertEquals(parent1.getPositions().length, positionSet.size(),
                "Offspring should contain all unique positions");
    }

    @Test
    void testCrossoverRange() {
        // Dummy mutation for testing purposes
        Mutation<TestOrder> dummyMutation = order -> order;

        // Create parents
        TestOrder parent1 = new TestOrder(new int[]{0, 1, 2, 3, 4, 5}, dummyMutation);
        TestOrder parent2 = new TestOrder(new int[]{5, 4, 3, 2, 1, 0}, dummyMutation);

        // Initialize the OrderCrossover
        OrderCrossover crossover = new OrderCrossover();

        // Apply the crossover multiple times to check range consistency
        for (int i = 0; i < 100; i++) {
            TestOrder offspring = crossover.apply(parent1, parent2);

            // Validate offspring is not null
            assertNotNull(offspring, "Offspring should not be null");

            // Validate the offspring has the correct length
            int[] offspringPositions = offspring.getPositions();
            assertEquals(parent1.getPositions().length, offspringPositions.length,
                    "Offspring should have the same length as the parents");

            // Validate the offspring contains all positions without duplicates
            Set<Integer> positionSet = new HashSet<>();
            for (int position : offspringPositions) {
                positionSet.add(position);
            }
            assertEquals(parent1.getPositions().length, positionSet.size(),
                    "Offspring should contain all unique positions");

            // Additional validation logic can be added here as needed
        }
    }
}
