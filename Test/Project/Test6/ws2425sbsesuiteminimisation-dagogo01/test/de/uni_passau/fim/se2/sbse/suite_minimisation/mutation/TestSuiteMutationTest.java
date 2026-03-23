package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestSuiteMutationTest {

    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;

    @BeforeEach
    void setup() {
        // Corrected: Provide a mutationProbability value (e.g., 0.5 for 50% chance)
        mutation = new TestSuiteMutation(0.5);
        crossover = mock(Crossover.class); // Mocked crossover for testing purposes
    }

    @Test
void testBitFlipOccurs() {
    int totalTestCases = 9;
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, totalTestCases);
    chromosome.setTestCases(Arrays.asList(0, 1, 0, 1, 0, 1, 0, 1, 0));

    boolean bitFlipped = false;
    for (int attempt = 0; attempt < 100; attempt++) {
        TestSuiteChromosome mutated = mutation.apply(chromosome);

        // Validate no index out of bounds
        assertEquals(chromosome.getTotalTestCases(), mutated.getTestCases().size(),
            "Mutated chromosome should have the same size.");

        for (int i = 0; i < chromosome.getTestCases().size(); i++) {
            if (!chromosome.getTestCases().get(i).equals(mutated.getTestCases().get(i))) {
                bitFlipped = true;
                break;
            }
        }
        if (bitFlipped) break;
    }

    assertTrue(bitFlipped, "At least one bit should be flipped after multiple attempts.");
}

@Test
void testMutationChangesChromosome() {
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 4);
    chromosome.setTestCases(Arrays.asList(0, 1, 1, 0));

    TestSuiteChromosome mutated = mutation.apply(chromosome);

    // Ensure the chromosome changes
    assertNotEquals(chromosome.getTestCases(), mutated.getTestCases(),
        "Mutation should change the chromosome. If no change, review mutation probability and logic.");
}


@Test
void testBitFlipEdgeCase() {
    Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(1.0); // Always mutate
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 4);
    chromosome.setTestCases(Arrays.asList(0, 0, 0, 0));

    TestSuiteChromosome mutated = mutation.apply(chromosome);

    // Ensure that at least one bit was flipped
    assertNotEquals(chromosome.getTestCases(), mutated.getTestCases(),
        "Mutation should flip at least one bit.");
}

    @Test
void testMutationNoEffectOnEmptyChromosome() {
    // Setup chromosome with zero total test cases
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 0);

    // Apply mutation
    TestSuiteChromosome mutated = mutation.apply(chromosome);

    // Ensure that the chromosome remains unchanged
    assertEquals(chromosome.getTestCases(), mutated.getTestCases(),
        "An empty chromosome should remain unchanged.");
}

    
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void testBitFlipDistribution() {
    // Assuming 9 test cases for this test
    int numberOfTestCases = 9;

    // Use mocks for mutation and crossover
    Mutation<TestSuiteChromosome> mockMutation = mock(Mutation.class);
    Crossover<TestSuiteChromosome> mockCrossover = mock(Crossover.class);

    // Create the chromosome with mocked mutation and crossover
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mockMutation, mockCrossover, numberOfTestCases);
    for (int i = 0; i < numberOfTestCases; i++) {
        chromosome.addTestCase(i);
    }

    TestSuiteMutation mutation = new TestSuiteMutation(0.1); // Adjust mutation rate as needed

    // Perform mutation and validate bounds
    TestSuiteChromosome mutatedChromosome = mutation.apply(chromosome);

    // Assert no indices are out of bounds
    for (int testCaseIndex : mutatedChromosome.getTestCases()) {
        assertTrue(testCaseIndex >= 0 && testCaseIndex < numberOfTestCases, 
            "Mutated index out of bounds: " + testCaseIndex);
    }
}
@Test
void testMutationHandlesEmptyChromosome() {
    Mutation<TestSuiteChromosome> mockMutation = mock(Mutation.class);
    Crossover<TestSuiteChromosome> mockCrossover = mock(Crossover.class);
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mockMutation, mockCrossover, 0);

    TestSuiteChromosome mutated = mutation.apply(chromosome);

    // Ensure that the chromosome remains unchanged
    assertEquals(chromosome.getTestCases(), mutated.getTestCases(),
        "Mutation should handle empty chromosomes gracefully.");
}

@Test
void testMutationIndexWithinBounds() {
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
    for (int i = 0; i < 10; i++) {
        chromosome.addTestCase(i);
    }

    TestSuiteChromosome mutated = mutation.apply(chromosome);

    for (int index : mutated.getTestCases()) {
        System.out.println("Mutated index: " + index);
        assertTrue(index >= 0 && index < chromosome.getTotalTestCases(), 
            "Index out of bounds: " + index);
    }
}


@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void testMutationHandlesTimeConstraint() {
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
    for (int i = 0; i < 10; i++) {
        chromosome.addTestCase(i);
    }

    TestSuiteChromosome mutated = mutation.apply(chromosome);

    // Validate bounds of mutation
    for (int index : mutated.getTestCases()) {
        assertTrue(index >= 0 && index < chromosome.getTotalTestCases(),
            "Index out of bounds in mutated chromosome: " + index);
    }
}
@Test
void testMutationProbabilityEffect() {
    Mutation<TestSuiteChromosome> mutation = new TestSuiteMutation(1.0); // Ensure mutation always happens
    TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 4);
    chromosome.setTestCases(Arrays.asList(0, 1, 1, 0));

    TestSuiteChromosome mutated = mutation.apply(chromosome);
    assertNotEquals(chromosome.getTestCases(), mutated.getTestCases(),
        "Chromosome should always change when mutation probability is 100%.");
}


}

