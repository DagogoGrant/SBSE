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
    // Example test cases
    boolean[] testCases = {true, false, true, false};
    CoverageTracker mockTracker = mock(CoverageTracker.class); // Mock CoverageTracker

    // Use mutation instance
    Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(0.1); // Ensure probability is handled
    Crossover<TestSuiteChromosome> crossover = Crossover.identity();

    // Create the TestSuiteChromosome
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);

    // Apply mutation
    TestSuiteChromosome mutatedChromosome = mutation.apply(chromosome);

    // Verify mutation flips exactly one bit
    int changes = 0;
    for (int i = 0; i < testCases.length; i++) {
        if (testCases[i] != mutatedChromosome.getTestCases()[i]) {
            changes++;
        }
    }

    assertEquals(1, changes, "Mutation should flip exactly one bit");
}

}
