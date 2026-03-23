package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for FitnessFunction implementations.
 */
class FitnessFunctionTest {

    @Test
    void testFitnessFunctionInstantiation() {
        // Example coverage matrix
        boolean[][] coverageMatrix = {
            {true, false, true},
            {false, true, false},
            {true, true, false}
        };

        // Instantiate APLC with a valid coverage matrix
        APLC aplc = new APLC(coverageMatrix);
        assertNotNull(aplc, "APLC instance should be created successfully.");
    }
}
