package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.SinglePointCrossover;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitFlipMutationTest {

    @Test
    void testMutationFlipsOneBit() {
        // Initial test suite
        boolean[] testSuite = {true, false, true, false};
        BitFlipMutation mutation = new BitFlipMutation();
        SinglePointCrossover crossover = new SinglePointCrossover();

        // Create parent chromosome with both mutation and crossover operators
        TestSuiteChromosome parent = new TestSuiteChromosome(testSuite, mutation, crossover);

        // Apply mutation
        TestSuiteChromosome mutated = mutation.apply(parent);

        // Ensure parent is unchanged
        assertArrayEquals(testSuite, parent.getTestSuite(), "Parent chromosome should not be modified");

        // Check one bit is flipped
        boolean[] mutatedSuite = mutated.getTestSuite();
        int flipCount = 0;
        for (int i = 0; i < testSuite.length; i++) {
            if (testSuite[i] != mutatedSuite[i]) flipCount++;
        }
        assertEquals(1, flipCount, "Exactly one bit should be flipped");

        // Ensure at least one bit is true
        assertTrue(
            containsAtLeastOneTrue(mutatedSuite),
            "Mutated chromosome must have at least one test case included"
        );
    }

    private boolean containsAtLeastOneTrue(boolean[] array) {
        for (boolean value : array) {
            if (value) return true;
        }
        return false;
    }
}
