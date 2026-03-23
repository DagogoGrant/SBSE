package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestOrderTest {

    @Test
    public void testConstructor_ValidInput() {
        // Arrange
        int[] validPositions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());

        // Act
        TestOrder testOrder = new TestOrder(mutation, validPositions);

        // Assert
        assertNotNull(testOrder);
        assertArrayEquals(validPositions, testOrder.getPositions());
    }

    @Test
    public void testConstructor_InvalidInput_Duplicates() {
        // Arrange
        int[] invalidPositions = {0, 1, 1, 3}; // Duplicate values
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(mutation, invalidPositions));
    }

    @Test
    public void testConstructor_InvalidInput_OutOfBounds() {
        // Arrange
        int[] invalidPositions = {0, 1, 2, 5}; // Out of bounds (should be from 0 to n-1)
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(mutation, invalidPositions));
    }

    @Test
    public void testIsValid_ValidArray() {
        // Arrange
        int[] validArray = {0, 1, 2, 3};

        // Act
        boolean result = TestOrder.isValid(validArray);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsValid_InvalidArray_Duplicates() {
        // Arrange
        int[] invalidArray = {0, 1, 2, 2};

        // Act
        boolean result = TestOrder.isValid(invalidArray);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsValid_InvalidArray_OutOfBounds() {
        // Arrange
        int[] invalidArray = {0, 1, 2, 4}; // Out of bounds

        // Act
        boolean result = TestOrder.isValid(invalidArray);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testDeepCopy() {
        // Arrange
        int[] positions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, positions);

        // Act
        TestOrder copy = testOrder.deepCopy();

        // Assert
        assertNotSame(testOrder, copy); // Ensure it's a different instance
        assertArrayEquals(testOrder.getPositions(), copy.getPositions()); // The positions should be identical
    }

    @Test
    public void testDeepCopy_ModifyOriginal() {
        // Arrange
        int[] positions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, positions);

        // Act
        TestOrder copy = testOrder.deepCopy();
        positions[0] = 99; // Modify original array

        // Assert
        assertNotEquals(positions[0], copy.getPositions()[0]); // The deep copy should be unaffected by changes to the original
    }

    @Test
    public void testSize() {
        // Arrange
        int[] positions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, positions);

        // Act
        int size = testOrder.size();

        // Assert
        assertEquals(4, size);
    }

    @Test
    public void testGetPositions() {
        // Arrange
        int[] positions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, positions);

        // Act
        int[] resultPositions = testOrder.getPositions();

        // Assert
        assertArrayEquals(positions, resultPositions);
    }
}
