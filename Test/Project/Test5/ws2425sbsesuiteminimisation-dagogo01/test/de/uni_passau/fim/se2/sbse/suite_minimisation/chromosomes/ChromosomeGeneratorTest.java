package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Random;

class ChromosomeGeneratorTest {

    private int numberTestCases;
    private CoverageTracker mockCoverageTracker;
    private TestSuiteMutation mutation;
    private TestSuiteCrossover crossover;
    private Random random;

    @BeforeEach
    void setUp() {
        numberTestCases = 10; // Define a valid number of test cases
        mockCoverageTracker = mock(CoverageTracker.class); // Mock the CoverageTracker
        mutation = new TestSuiteMutation(0.1); // Define a mutation with a 10% probability
        crossover = new TestSuiteCrossover(); // Define a crossover
        random = new Random(42); // Use a fixed seed for predictable results
    }

    @Test
    void testChromosomeGeneration() {
        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            mockCoverageTracker,
            mutation,
            crossover,
            random
        );

        TestSuiteChromosome chromosome = generator.get();

        assertNotNull(chromosome, "Generated chromosome should not be null");
        assertEquals(numberTestCases, chromosome.getTestCases().length, "Chromosome size should match number of test cases");

        // Verify that at least one test case is selected
        boolean atLeastOneSelected = false;
        for (boolean testCase : chromosome.getTestCases()) {
            if (testCase) {
                atLeastOneSelected = true;
                break;
            }
        }
        assertTrue(atLeastOneSelected, "At least one test case should be selected");
    }

    @Test
    void testChromosomeGenerationWithInvalidInputs() {
        // Test with invalid number of test cases
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(0, mockCoverageTracker, mutation, crossover, random)
        );
        assertEquals("Number of test cases must be positive.", exception.getMessage());

        // Test with null CoverageTracker
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, null, mutation, crossover, random)
        );
        assertEquals("CoverageTracker cannot be null", exception.getMessage());

        // Test with null Mutation
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, mockCoverageTracker, null, crossover, random)
        );
        assertEquals("Mutation operator cannot be null", exception.getMessage());

        // Test with null Crossover
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, mockCoverageTracker, mutation, null, random)
        );
        assertEquals("Crossover operator cannot be null", exception.getMessage());
    }
}
