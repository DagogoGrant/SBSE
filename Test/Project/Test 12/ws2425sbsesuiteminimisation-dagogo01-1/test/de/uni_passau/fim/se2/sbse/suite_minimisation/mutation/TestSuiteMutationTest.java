package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSuiteMutationTest {

    @Test
    void testMutationFlipsOneBit() {
        boolean[] testCases = {true, false, true, false};
        CoverageTracker mockTracker = mock(CoverageTracker.class);

        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(1.0); // Mutation probability of 1.0
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);
        TestSuiteChromosome mutatedChromosome = mutation.apply(chromosome);

        int changes = 0;
        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i] != mutatedChromosome.getTestCases()[i]) {
                changes++;
            }
        }

        assertEquals(1, changes, "Mutation should flip exactly one bit");
    }


    @Test
    void testMutationThrowsExceptionForNullChromosome() {
        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(0.5);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mutation.apply(null));
        assertEquals("Chromosome or its test cases cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testMutationThrowsExceptionForEmptyChromosome() {
        boolean[] testCases = {};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(0.5);
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mutation.apply(chromosome));
        assertEquals("Chromosome or its test cases cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testInvalidMutationProbability() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new TestSuiteMutation(-0.1));
        assertEquals("Mutation probability must be between 0.0 and 1.0", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> new TestSuiteMutation(1.1));
        assertEquals("Mutation probability must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    void testMutationWithOneGene() {
        boolean[] testCases = {true};
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(1.0); // Force mutation
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);
        TestSuiteChromosome mutatedChromosome = mutation.apply(chromosome);

        assertEquals(1, mutatedChromosome.getTestCases().length, "Chromosome should still have one gene");
        assertNotEquals(testCases[0], mutatedChromosome.getTestCases()[0], "Gene should be flipped");
    }

    @Test
    void testMutationHandlesLargeChromosomes() {
        boolean[] testCases = new boolean[1000];
        CoverageTracker mockTracker = mock(CoverageTracker.class);
        Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(1.0); // Ensure mutation occurs
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);
        TestSuiteChromosome mutatedChromosome = mutation.apply(chromosome);

        assertEquals(1000, mutatedChromosome.getTestCases().length, "Chromosome size should remain unchanged");
    }
}
