package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestOrderGeneratorTest {

    @Test
    public void testConstructor_ValidInput() {
        // Arrange
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        // Act
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 5);

        // Assert
        assertNotNull(generator);
    }

    @Test
    public void testConstructor_InvalidTestCases() {
        // Arrange
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TestOrderGenerator(random, mutation, 0));
        assertThrows(IllegalArgumentException.class, () -> new TestOrderGenerator(random, mutation, -1));
    }

    @Test
    public void testConstructor_NullArguments() {
        // Arrange
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> new TestOrderGenerator(null, mutation, 5));
        assertThrows(NullPointerException.class, () -> new TestOrderGenerator(random, null, 5));
    }

    @Test
    public void testGet_ValidOrdering() {
        // Arrange
        Random random = new Random(42); // Fixed seed for reproducibility
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 4);

        // Act
        TestOrder testOrder = generator.get();

        // Assert
        assertNotNull(testOrder);
        assertEquals(4, testOrder.size());

        int[] positions = testOrder.getPositions();
        assertTrue(TestOrder.isValid(positions)); // Ensure the generated ordering is valid

        // Ensure all elements from 0 to 3 are present
        for (int i = 0; i < 4; i++) {
            assertTrue(contains(positions, i));
        }
    }

    @Test
    public void testGet_Randomness() {
        // Arrange
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 6);

        // Act
        TestOrder firstOrder = generator.get();
        TestOrder secondOrder = generator.get();

        // Assert
        assertNotNull(firstOrder);
        assertNotNull(secondOrder);
        assertFalse(areEqual(firstOrder.getPositions(), secondOrder.getPositions())); // Should ideally be different
    }

    private boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    private boolean areEqual(int[] array1, int[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
}
