package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestOrderGeneratorTest {

    @Test
    void testGeneratorProducesDifferentOrders() {
        Random random = new Random();  // Use a non-fixed seed to allow variability
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 5);

        // Generate multiple orders
        Set<String> generatedOrders = new HashSet<>();
        int numberOfOrders = 10;

        for (int i = 0; i < numberOfOrders; i++) {
            TestOrder order = generator.get();
            generatedOrders.add(arrayToString(order.getPositions()));
        }

        // Ensure that at least some of the generated orders are different
        assertTrue(generatedOrders.size() > 1, "Generated orders should not always be identical.");
    }

    @Test
    void testGeneratorProducesValidOrder() {
        Random random = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 5);
        TestOrder order = generator.get();

        assertTrue(TestOrder.isValid(order.getPositions()), "The generated order must be valid.");
    }

    @Test
    void testGeneratorProducesCorrectSize() {
        Random random = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, 5);
        TestOrder order = generator.get();

        assertEquals(5, order.size(), "The generated order must have the correct size.");
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int value : array) {
            sb.append(value).append(",");
        }
        return sb.toString();
    }
}
