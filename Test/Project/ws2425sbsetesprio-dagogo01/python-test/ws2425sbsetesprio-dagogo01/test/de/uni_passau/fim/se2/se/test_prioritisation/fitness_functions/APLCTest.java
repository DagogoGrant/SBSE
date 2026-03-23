package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APLCTest {

    @Test
    void testApplyAsDouble() {
        // Create a dummy coverage matrix
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };

        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Create a TestOrder instance
        int[] positions = {0, 1, 2};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Create an APLC instance with the coverage matrix
        APLC aplc = new APLC(coverageMatrix);

        // Calculate APLC value
        double result = aplc.applyAsDouble(testOrder);

        // Validate the result (expecting a non-negative value)
        assertTrue(result >= 0, "APLC value should be non-negative");
    }

    @Test
    void testMaximise() {
        // Create a dummy coverage matrix
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };

        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Create a TestOrder instance
        int[] positions = {0, 1, 2};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Create an APLC instance with the coverage matrix
        APLC aplc = new APLC(coverageMatrix);

        // Calculate and validate maximise function
        double maximiseResult = aplc.maximise(testOrder);
        assertEquals(aplc.applyAsDouble(testOrder), maximiseResult,
                "Maximise should return the same value as applyAsDouble");
    }

    @Test
    void testMinimise() {
        // Create a dummy coverage matrix
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, false}
        };

        // Create a dummy mutation for TestOrder
        Mutation<TestOrder> dummyMutation = testOrder -> testOrder;

        // Create a TestOrder instance
        int[] positions = {0, 1, 2};
        TestOrder testOrder = new TestOrder(positions, dummyMutation);

        // Create an APLC instance with the coverage matrix
        APLC aplc = new APLC(coverageMatrix);

        // Calculate and validate minimise function
        double minimiseResult = aplc.minimise(testOrder);
        assertEquals(-aplc.applyAsDouble(testOrder), minimiseResult,
                "Minimise should return the negative value of applyAsDouble");
    }
}
