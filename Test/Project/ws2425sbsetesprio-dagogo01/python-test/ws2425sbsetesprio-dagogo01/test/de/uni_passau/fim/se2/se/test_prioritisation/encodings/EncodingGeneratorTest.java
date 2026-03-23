package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EncodingGeneratorTest {

    @Test
    void testGenerate() {
        Random random = new Random();
        int numTestCases = 5;

        // Provide a dummy mutation for testing
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Updated constructor to match the class definition
        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, dummyMutation);

        TestOrder testOrder = generator.generate();

        assertNotNull(testOrder);
        assertEquals(numTestCases, testOrder.getPositions().length);
    }
}
