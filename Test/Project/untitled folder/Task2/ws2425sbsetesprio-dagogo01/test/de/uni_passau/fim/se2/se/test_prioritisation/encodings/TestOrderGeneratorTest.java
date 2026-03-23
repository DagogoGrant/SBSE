package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class TestOrderGeneratorTest {

    @Test
    public void testGeneratorCreatesValidTestOrder() {
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        int testCases = 5;
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, testCases);

        TestOrder testOrder = generator.get();
        assertNotNull(testOrder, "Generated TestOrder should not be null");
        assertEquals(testCases, testOrder.size(), "TestOrder size should match the number of test cases");
        assertTrue(TestOrder.isValid(testOrder.getPositions()), "Generated TestOrder should be valid");
    }

    @Test
    public void testGeneratorThrowsExceptionForInvalidTestCases() {
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        int invalidTestCases = -1;

        assertThrows(IllegalArgumentException.class, () -> {
            new TestOrderGenerator(random, mutation, invalidTestCases);
        }, "Generator should throw an exception for negative number of test cases");
    }

    @Test
    public void testGeneratorProducesDifferentOrders() {
        Random random = new Random();
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        int testCases = 5;
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, testCases);

        TestOrder firstOrder = generator.get();
        TestOrder secondOrder = generator.get();

        // With randomness, it's possible they could be the same, but unlikely.
        // This test aims to check that the generator does not always produce the same output.
        assertFalse(Arrays.equals(firstOrder.getPositions(), secondOrder.getPositions()), "Generated orders should not always be identical");
    }
}
