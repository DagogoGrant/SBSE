package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteMutationTest {

    @Test
    void testMutationAddsOrRemovesTestCase() {
        int totalTestCases = 10;
        double mutationProbability = 1.0; // Force mutation
        
        // Define mutation and placeholder crossover operators
        TestSuiteMutation mutation = new TestSuiteMutation(mutationProbability);
        Crossover<TestSuiteChromosome> identityCrossover = Crossover.identity();

        // Create a TestSuiteChromosome with valid mutation and crossover
        TestSuiteChromosome parent = new TestSuiteChromosome(mutation, identityCrossover, totalTestCases);
        int originalSize = parent.getTestCases().size();

        // Apply mutation
        TestSuiteChromosome mutated = mutation.apply(parent);
        int mutatedSize = mutated.getTestCases().size();

        // Assertions
        assertNotEquals(originalSize, mutatedSize, "Mutation should change the chromosome size");
        assertTrue(mutated.getTestCases().size() >= 1, "Mutated chromosome should have at least one test case");
    }
}
