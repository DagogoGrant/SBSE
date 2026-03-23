package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCrossoverTest {

    @Test
    public void testConstructor_NullRandom() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new OrderCrossover(null));
    }

    @Test
    public void testApply_SimpleCrossover() {
        // Arrange
        Random random = new Random(42);
        OrderCrossover crossover = new OrderCrossover(random);

        int[] parent1Positions = {0, 1, 2, 3};
        int[] parent2Positions = {3, 2, 1, 0};

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrder parent1 = new TestOrder(mutation, parent1Positions);
        TestOrder parent2 = new TestOrder(mutation, parent2Positions);

        // Act
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Assert
        assertNotNull(offspring);
        assertEquals(parent1.size(), offspring.size());
        assertTrue(TestOrder.isValid(offspring.getPositions()), "Offspring must be a valid permutation of test cases");
    }

    @Test
    public void testApply_IdenticalParents() {
        // Arrange
        Random random = new Random();
        OrderCrossover crossover = new OrderCrossover(random);

        int[] parentPositions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrder parent1 = new TestOrder(mutation, parentPositions);
        TestOrder parent2 = new TestOrder(mutation, parentPositions);

        // Act
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Assert
        assertNotNull(offspring);
        assertEquals(parent1.size(), offspring.size());
        assertArrayEquals(parentPositions, offspring.getPositions(), "Offspring should be identical to parents when parents are identical");
    }

    @Test
    public void testApply_DifferentLengthParents_Invalid() {
        // Arrange
        Random random = new Random();
        OrderCrossover crossover = new OrderCrossover(random);

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrder parent1 = new TestOrder(mutation, new int[]{0, 1, 2});
        TestOrder parent2 = new TestOrder(mutation, new int[]{0, 1, 2, 3});

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> crossover.apply(parent1, parent2));
    }

    @Test
    public void testApply_CrossoverProducesValidOffspring() {
        // Arrange
        Random random = new Random();
        OrderCrossover crossover = new OrderCrossover(random);

        int[] parent1Positions = {0, 1, 2, 3, 4};
        int[] parent2Positions = {4, 3, 2, 1, 0};

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrder parent1 = new TestOrder(mutation, parent1Positions);
        TestOrder parent2 = new TestOrder(mutation, parent2Positions);

        // Act
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Assert
        assertNotNull(offspring);
        assertEquals(parent1.size(), offspring.size());
        assertTrue(TestOrder.isValid(offspring.getPositions()), "Offspring must be a valid permutation of test cases");
    }
}
