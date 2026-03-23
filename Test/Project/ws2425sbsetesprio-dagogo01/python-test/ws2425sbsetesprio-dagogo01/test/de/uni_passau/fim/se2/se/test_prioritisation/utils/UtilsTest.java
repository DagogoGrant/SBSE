package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testGetTestCaseOrder() {
        // Define test case names
        String[] testCaseNames = {"TestA", "TestB", "TestC"};

        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Define positions for TestOrder
        int[] positions = {2, 0, 1};

        // Create TestOrder instance with valid arguments
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Get the ordered test case string
        String result = Utils.getTestCaseOrder(testCaseNames, testOrder);

        // Validate the result
        assertEquals("TestC, TestA, TestB", result, "The test case order should match the given positions.");
    }

    @Test
    void testGetTestCaseOrderWithInvalidPosition() {
        // Define test case names
        String[] testCaseNames = {"TestA", "TestB"};

        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Define invalid positions
        int[] positions = {0, 2}; // 2 is out of bounds for testCaseNames

        // Create TestOrder instance
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Validate that an exception is thrown for invalid positions
        assertThrows(IllegalArgumentException.class, () -> {
            Utils.getTestCaseOrder(testCaseNames, testOrder);
        });
    }
}
