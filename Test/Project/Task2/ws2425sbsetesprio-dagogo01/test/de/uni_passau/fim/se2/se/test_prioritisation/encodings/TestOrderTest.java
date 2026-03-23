package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TestOrderTest {

    @Test
    public void testTestOrderIsValid() {
        int[] validOrder = {0, 1, 2, 3};
        assertTrue(TestOrder.isValid(validOrder), "Valid order should be valid");

        int[] invalidOrder = {0, 1, 1, 3};
        assertFalse(TestOrder.isValid(invalidOrder), "Duplicate values should make the order invalid");
    }

    @Test
    public void testTestOrderCreation() {
        int[] positions = {0, 1, 2, 3};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, positions);
        assertNotNull(testOrder, "TestOrder should be created successfully");
        assertEquals(positions.length, testOrder.size(), "TestOrder size should match the positions length");
        assertArrayEquals(positions, testOrder.getPositions(), "TestOrder positions should match the input positions");
    }
}
