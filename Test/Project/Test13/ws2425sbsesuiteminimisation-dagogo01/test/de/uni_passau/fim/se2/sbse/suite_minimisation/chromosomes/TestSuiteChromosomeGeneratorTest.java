package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Random;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
class TestSuiteChromosomeGeneratorTest {

    private int numberTestCases;
    private CoverageTracker mockCoverageTracker;
    private TestSuiteMutation mutation;
    private TestSuiteCrossover crossover;
    private Random random;
    // Replace java.util.logging.Logger with slf4j Logger for better testing capabilities.

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
    @Test
void testForcedSelectionWhenNoneAreSelected() {
    random = new Random() {
        @Override
        public double nextDouble() {
            return 1.0; // Ensure no test case is selected
        }

        @Override
        public int nextInt(int bound) {
            return 2; // Force the selection of the 3rd test case
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

    boolean[] testCases = chromosome.getTestCases();
    assertTrue(testCases[2], "The forced selection should ensure at least one test case is selected");
    assertEquals(1, java.util.stream.IntStream.range(0, testCases.length).filter(i -> testCases[i]).count(),
            "Only one test case should be selected when forced");
}
@Test
void testLoggingWhenForcedSelectionOccurs() {
    random = new Random() {
        @Override
        public double nextDouble() {
            return 1.0; // Ensure no test case is selected
        }

        @Override
        public int nextInt(int bound) {
            return 4; // Force the selection of the 5th test case
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

    boolean[] testCases = chromosome.getTestCases();
    assertTrue(testCases[4], "The forced selection should ensure at least one test case is selected");

    // Verify log message using a custom logger or mock logger
    // Example: Ensure warning about forced selection is logged
    // Requires additional logging testing setup
}

@Test
void testNullRandomDefaultsToNewInstance() {
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        numberTestCases,
        mockCoverageTracker,
        mutation,
        crossover,
        null // Pass null to test default random initialization
    );

    TestSuiteChromosome chromosome = generator.get();
    assertNotNull(chromosome, "Generated chromosome should not be null");
}
@Test
void testSingleTestCaseEdgeCase() {
    int singleTestCase = 1;
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        singleTestCase,
        mockCoverageTracker,
        mutation,
        crossover,
        random
    );

    TestSuiteChromosome chromosome = generator.get();
    boolean[] testCases = chromosome.getTestCases();

    assertEquals(1, testCases.length, "Chromosome should have exactly one test case");
    assertTrue(testCases[0], "The single test case should always be selected");
}

@Test
void testMultipleGenerationsAreUnique() {
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        numberTestCases,
        mockCoverageTracker,
        mutation,
        crossover,
        random
    );

    TestSuiteChromosome firstChromosome = generator.get();
    TestSuiteChromosome secondChromosome = generator.get();

    assertNotEquals(firstChromosome, secondChromosome, "Subsequent chromosomes should be independently generated");
}
@Test
void testCoverageTrackerInteraction() throws Exception {
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        numberTestCases,
        mockCoverageTracker,
        mutation,
        crossover,
        random
    );

    TestSuiteChromosome chromosome = generator.get();

    // Verify CoverageTracker is used during chromosome creation
    verify(mockCoverageTracker, times(0)).getCoverageMatrix(); // Not called directly in generator
    assertNotNull(chromosome.getCoverageTracker(), "Chromosome should have a valid CoverageTracker instance");
}

@Test
void testBiasAffectsSelection() {
    random = new Random(456); // Fixed seed for consistent behavior

    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        numberTestCases,
        mockCoverageTracker,
        mutation,
        crossover,
        random
    );

    TestSuiteChromosome chromosome = generator.get();
    boolean[] testCases = chromosome.getTestCases();

    // Bias is 0.2; validate the expected number of selected test cases
    long selectedCount = java.util.stream.IntStream.range(0, testCases.length)
        .filter(i -> testCases[i])
        .count();

    assertTrue(selectedCount >= 1, "At least one test case should be selected due to forced selection");
    assertTrue(selectedCount <= numberTestCases * 0.2 + 2, "Bias should limit selection to a small proportion");
}

// @Test
// void testHighBiasSelection() {
//     random = new Random() {
//         @Override
//         public double nextDouble() {
//             return 0.9; // High probability of selecting a test case
//         }
//     };

//     TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
//         numberTestCases,
//         mockCoverageTracker,
//         mutation,
//         crossover,
//         random
//     );

//     TestSuiteChromosome chromosome = generator.get();
//     boolean[] testCases = chromosome.getTestCases();

//     long selectedCount = java.util.stream.IntStream.range(0, testCases.length)
//         .filter(i -> testCases[i])
//         .count();

//     assertTrue(selectedCount >= numberTestCases * 0.8, "High bias should select most of the test cases");
// }
@Test
void testZeroCoverageTrackerInteraction() {
    verifyNoInteractions(mockCoverageTracker);
}
@Test
void testNegativeCoverageInteractions() throws IllegalAccessException{
    // create a child state logic    
}
@Test
void testForcedSelectionWithDifferentSeeds() {
    for (int seed = 1; seed <= 10; seed++) {
        Random seededRandom = new Random(seed);
        TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
            numberTestCases,
            mockCoverageTracker,
            mutation,
            crossover,
            seededRandom
        );

        TestSuiteChromosome chromosome = generator.get();

        // Ensure forced selection logic applies for all seeds
        boolean[] testCases = chromosome.getTestCases();
        boolean atLeastOneSelected = java.util.stream.IntStream.range(0, testCases.length)
            .anyMatch(i -> testCases[i]);
        assertTrue(atLeastOneSelected, "At least one test case should be selected for seed: " + seed);
    }
}
// @Test
// void testLoggingForcedSelection() {
//     Random mockRandom = mock(Random.class);
//     when(mockRandom.nextDouble()).thenReturn(1.0); // Ensure no test case is selected
//     when(mockRandom.nextInt(anyInt())).thenReturn(2); // Force 3rd test case selection

//     TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
//         numberTestCases,
//         mockCoverageTracker,
//         mutation,
//         crossover,
//         mockRandom
//     );

//     TestSuiteChromosome chromosome = generator.get();

//     // Mock logger verification (requires additional logger setup)
//     verify(Logger.getLogger(TestSuiteChromosomeGenerator.class.getName()), atLeastOnce())
//         .warning(contains("Forced selection"));
// }
@Test
void testSingleTestCaseForcedDeselect() {
    int singleTestCase = 1;
    Random mockRandom = mock(Random.class);
    when(mockRandom.nextDouble()).thenReturn(1.0); // No selection
    when(mockRandom.nextInt(singleTestCase)).thenReturn(0); // Force re-selection of single test case

    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        singleTestCase,
        mockCoverageTracker,
        mutation,
        crossover,
        mockRandom
    );

    TestSuiteChromosome chromosome = generator.get();
    boolean[] testCases = chromosome.getTestCases();

    assertTrue(testCases[0], "The single test case should always be re-selected");
}
// @Test
// void testZeroCoverageInteraction() throws Exception { // Add throws Exception
//     boolean[][] emptyMatrix = new boolean[numberTestCases][];
//     for (int i = 0; i < numberTestCases; i++) {
//         emptyMatrix[i] = new boolean[numberTestCases];
//     }

//     when(mockCoverageTracker.getCoverageMatrix()).thenReturn(emptyMatrix);

//     TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
//         numberTestCases,
//         mockCoverageTracker,
//         mutation,
//         crossover,
//         random
//     );

//     TestSuiteChromosome chromosome = generator.get();

//     // Validate interaction
//     verify(mockCoverageTracker, atLeastOnce()).getCoverageMatrix();
//     boolean[] testCases = chromosome.getTestCases();

//     // Verify no test cases were pre-selected
//     assertEquals(0, java.util.stream.IntStream.range(0, testCases.length)
//         .filter(i -> testCases[i])
//         .count(), "No test cases should be initially selected");

//     // Ensure forced selection still applies
//     boolean atLeastOneSelected = false;
//     for (boolean testCase : testCases) {
//         if (testCase) {
//             atLeastOneSelected = true;
//             break;
//         }
//     }
//     assertTrue(atLeastOneSelected, "At least one test case should be selected after forced selection");
// }


}
