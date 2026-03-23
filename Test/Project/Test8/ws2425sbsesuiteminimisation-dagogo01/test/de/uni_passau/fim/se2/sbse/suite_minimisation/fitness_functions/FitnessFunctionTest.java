package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FitnessFunctionTest {

    @SuppressWarnings("unchecked")
@Test
void testSizeFitnessFunction() throws Exception {
    // Mock dependencies
    CoverageTracker mockCoverageTracker = mock(CoverageTracker.class);
    Mutation<TestSuiteChromosome> mockMutation = (Mutation<TestSuiteChromosome>) mock(Mutation.class);
    Crossover<TestSuiteChromosome> mockCrossover = (Crossover<TestSuiteChromosome>) mock(Crossover.class);

    // Create a test chromosome
    boolean[] testCases = {true, false, true, true};
    double expectedSizeFitness = 3.0 / 4.0; // 3 selected out of 4 (normalized)
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    // Test SizeFitnessFunction with weight parameter
    double sizeWeight = 0.5; // Example weight for the size fitness function
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);
    double sizeFitness = sizeFitnessFunction.applyAsDouble(chromosome);

    // Assert size fitness matches the expected value
    assertEquals(expectedSizeFitness * sizeWeight, sizeFitness, 1e-6, "Size fitness calculation is incorrect!");
}


    @SuppressWarnings("unchecked")
    @Test
void testCoverageFitnessFunction() throws Exception {
    // Mock dependencies
    CoverageTracker mockCoverageTracker = mock(CoverageTracker.class);
    Mutation<TestSuiteChromosome> mockMutation = (Mutation<TestSuiteChromosome>) mock(Mutation.class);
    Crossover<TestSuiteChromosome> mockCrossover = (Crossover<TestSuiteChromosome>) mock(Crossover.class);

    // Define coverage matrix and mock its behavior
    boolean[][] coverageMatrix = {
        {true, false, true}, // Test case 1 covers lines 1, 3
        {false, true, false}, // Test case 2 covers line 2
        {true, true, true}   // Test case 3 covers all lines
    };
    when(mockCoverageTracker.getCoverageMatrix()).thenReturn(coverageMatrix);

    // Create a test chromosome
    boolean[] testCases = {true, false, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    // Test CoverageFitnessFunction with weight parameter
    double weightCoefficient = 0.5; // Example weight for the coverage fitness function
    CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, weightCoefficient);
    double coverageFitness = coverageFitnessFunction.applyAsDouble(chromosome);

    // Add appropriate assertion based on your computation logic
    assertTrue(coverageFitness >= 0, "Coverage fitness calculation should be non-negative!");
}

}
