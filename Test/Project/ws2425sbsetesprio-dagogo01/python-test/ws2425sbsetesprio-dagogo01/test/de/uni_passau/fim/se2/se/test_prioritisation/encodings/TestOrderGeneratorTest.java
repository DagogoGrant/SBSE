package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TestOrderGeneratorTest {

    @Test
    void testGenerateWithRandomPositions() {
        int numTestCases = 5;
        Random random = new Random();
        Mutation<TestOrder> mutation = new ShiftToBeginningMutation(random);

        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, mutation);
        TestOrder testOrder = generator.generate();

        assertNotNull(testOrder, "Generated TestOrder should not be null.");
        assertEquals(numTestCases, testOrder.getPositions().length, "Generated positions should match the number of test cases.");
        assertTrue(isValid(testOrder.getPositions(), numTestCases), "Generated positions should be valid.");
    }

    @Test
    void testGenerateWithFixedPositions() {
        int[] fixedPositions = {2, 4, 1, 0, 3};
        int numTestCases = fixedPositions.length;
        Random random = new Random();
        Mutation<TestOrder> mutation = new ShiftToBeginningMutation(random);

        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, mutation, fixedPositions);
        TestOrder testOrder = generator.generate();

        assertNotNull(testOrder, "Generated TestOrder should not be null.");
        assertArrayEquals(fixedPositions, testOrder.getPositions(), "Generated positions should match the fixed positions.");
        assertTrue(isValid(testOrder.getPositions(), numTestCases), "Generated positions should be valid.");
    }

    @Test
    void testInvalidNumTestCases() {
        Random random = new Random();
        Mutation<TestOrder> mutation = new ShiftToBeginningMutation(random);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TestOrderGenerator(random, 0, mutation),
                "Creating a generator with zero test cases should throw an exception."
        );
        assertEquals("Number of test cases must be greater than 0.", exception.getMessage());
    }

    @Test
    void testGenerateEnsuresRandomness() {
        int numTestCases = 5;
        Random random = new Random();
        Mutation<TestOrder> mutation = new ShiftToBeginningMutation(random);

        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, mutation);

        // Generate multiple TestOrders and check that at least some are different
        TestOrder testOrder1 = generator.generate();
        TestOrder testOrder2 = generator.generate();
        TestOrder testOrder3 = generator.generate();

        assertNotNull(testOrder1, "First generated TestOrder should not be null.");
        assertNotNull(testOrder2, "Second generated TestOrder should not be null.");
        assertNotNull(testOrder3, "Third generated TestOrder should not be null.");

        // Validate randomness by ensuring at least one pair of TestOrders is not equal
        boolean isDifferent = !testOrder1.equals(testOrder2) || !testOrder2.equals(testOrder3) || !testOrder1.equals(testOrder3);
        assertTrue(isDifferent, "Generated TestOrders should be different to ensure randomness.");
    }

    /**
     * Helper method to validate positions array.
     *
     * @param positions    The positions array to validate.
     * @param numTestCases The expected number of test cases.
     * @return True if valid, false otherwise.
     */
    private boolean isValid(int[] positions, int numTestCases) {
        if (positions.length != numTestCases) return false;

        boolean[] seen = new boolean[numTestCases];
        for (int pos : positions) {
            if (pos < 0 || pos >= numTestCases || seen[pos]) {
                return false;
            }
            seen[pos] = true;
        }
        return true;
    }
}
