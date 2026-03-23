package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteMutationTest {

    @Test
    void testBitFlipMutation() {
        Random random = new Random(42);

        // Provide identity mutation and crossover for testing
        Mutation<TestSuiteChromosome> mutation = chromosome -> chromosome.copy();
        Crossover<TestSuiteChromosome> crossover = (parent1, parent2) -> null; // Not used in this test

        boolean[] genes = {true, false, true, false};

        TestSuiteChromosome parent = new TestSuiteChromosome(genes, mutation, crossover);
        TestSuiteMutation testMutation = new TestSuiteMutation(0.5, random); // Include mutation probability

        TestSuiteChromosome mutated = testMutation.apply(parent);

        assertNotNull(mutated, "Mutated chromosome should not be null.");
        assertNotSame(parent, mutated, "Mutation should create a new chromosome.");
        assertTrue(isValid(mutated.getTestCases()), "Mutated chromosome should be valid.");
    }

    private boolean isValid(boolean[] genes) {
        for (boolean gene : genes) {
            if (gene) return true;
        }
        return false;
    }
}
