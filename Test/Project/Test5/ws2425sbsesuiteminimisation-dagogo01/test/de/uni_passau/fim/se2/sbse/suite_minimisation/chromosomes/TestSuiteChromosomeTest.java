package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestSuiteChromosomeTest {

    @Test
    void testCoverageFitness_fullCoverage() throws Exception {
        // Mock CoverageTracker to return full coverage
        CoverageTracker mockTracker = Mockito.mock(CoverageTracker.class);
        Mockito.when(mockTracker.getCoverageMatrix()).thenReturn(new boolean[][] {
            {true, true, true},
            {true, true, true},
        });
    
        boolean[] testCases = {true, true}; // Select both test cases
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
            testCases, mockTracker, Mutation.identity(), Crossover.identity()
        );
    
        assertEquals(1.0, chromosome.computeCoverageFitness(), 0.001, "Full coverage should be 1.0");
    }
    
    @Test
public void testCoverageFitness_partialCoverage() throws Exception {
    boolean[] testCases = {true, false, true}; // Selected test cases
    CoverageTracker coverageTracker = mock(CoverageTracker.class);
    Mutation<TestSuiteChromosome> mutation = mock(Mutation.class);
    Crossover<TestSuiteChromosome> crossover = mock(Crossover.class);

    // Mock the coverage matrix to simulate partial coverage
    boolean[][] mockCoverageMatrix = {
        {true, false, false},  // Test case 0 covers line 0
        {false, false, false}, // Test case 1 does not cover any lines
        {true, true, false}   // Test case 2 covers lines 0 and 1
    };
    when(coverageTracker.getCoverageMatrix()).thenReturn(mockCoverageMatrix);

    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, coverageTracker, mutation, crossover);

    double expectedFitness = 0.666; // Partial coverage fitness
    double actualFitness = chromosome.computeCoverageFitness();
    assertEquals(expectedFitness, actualFitness, 0.001); // Allow delta for floating-point comparison
}


   @Test
void testSizeFitness() {
    // Test setup
    boolean[] testCases = {true, false, true, false}; // Example test cases
    CoverageTracker mockTracker = mock(CoverageTracker.class); // Mock CoverageTracker
    Mutation<TestSuiteChromosome> mutation = Mutation.identity();
    Crossover<TestSuiteChromosome> crossover = Crossover.identity();

    // Create the TestSuiteChromosome object
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockTracker, mutation, crossover);

    // Calculate the size fitness
    double sizeFitness = chromosome.computeSizeFitness();

    // Assert the size fitness
    assertEquals(0.5, sizeFitness, 0.001, "Size fitness should be 0.5");
}

}
