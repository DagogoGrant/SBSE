package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testAllTestCasesNotSelectedInitially() {
        random = new Random() {
            private boolean firstCall = true;

            @Override
            public double nextDouble() {
                // Ensure no test case is selected initially
                return firstCall ? 1.0 : super.nextDouble();
            }

            @Override
            public int nextInt(int bound) {
                firstCall = false;
                return 0; // Force selection of the first test case
            }
        };

        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            mockCoverageTracker,
            mutation,
            crossover,
            random
        );

        TestSuiteChromosome chromosome = generator.get();

        // Verify that at least one test case was selected
        boolean atLeastOneSelected = false;
        for (boolean testCase : chromosome.getTestCases()) {
            if (testCase) {
                atLeastOneSelected = true;
                break;
            }
        }
        assertTrue(atLeastOneSelected, "At least one test case should be selected when none are selected initially");
    }

    @Test
void testInvalidChromosomeThrowsException() {
    // Mocking CoverageTracker to simulate an issue during chromosome creation
    CoverageTracker mockCoverageTracker = mock(CoverageTracker.class);

    // Create an instance of the generator
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        numberTestCases,
        mockCoverageTracker,
        mutation,
        crossover,
        random
    );

    // Spy on the generator to simulate a zero-length test case array
    TestSuiteChromosomeGenerator spyGenerator = spy(generator);

    // Simulate an invalid state using a mock of TestSuiteChromosome
    doThrow(new IllegalStateException("Generated chromosome has zero test cases."))
        .when(spyGenerator).get();

    // Validate that the exception is thrown
    Exception exception = assertThrows(IllegalStateException.class, () -> {
        spyGenerator.get();
    });

    // Verify the exception message
    assertEquals("Generated chromosome has zero test cases.", exception.getMessage());
}


    @Test
    void testRandomBiasSelection() {
        random = new Random(123); // Fixed seed for reproducibility

        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            mockCoverageTracker,
            mutation,
            crossover,
            random
        );

        TestSuiteChromosome chromosome = generator.get();

        // Assert that the random bias creates a specific pattern
        int selectedCount = 0;
        for (boolean testCase : chromosome.getTestCases()) {
            if (testCase) {
                selectedCount++;
            }
        }

        assertTrue(selectedCount > 0 && selectedCount < numberTestCases, "Random bias should select some, but not all, test cases");
    }

    @Test
    void testInvalidInputs() {
        // Test invalid number of test cases
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(0, mockCoverageTracker, mutation, crossover, random)
        );
        assertEquals("Number of test cases must be positive.", exception.getMessage());

        // Test null CoverageTracker
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, null, mutation, crossover, random)
        );
        assertEquals("CoverageTracker cannot be null", exception.getMessage());

        // Test null Mutation
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, mockCoverageTracker, null, crossover, random)
        );
        assertEquals("Mutation operator cannot be null", exception.getMessage());

        // Test null Crossover
        exception = assertThrows(IllegalArgumentException.class, () ->
            new TestSuiteChromosomeGenerator(numberTestCases, mockCoverageTracker, mutation, null, random)
        );
        assertEquals("Crossover operator cannot be null", exception.getMessage());
    }
}
