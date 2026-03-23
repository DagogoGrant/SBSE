package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FitnessFunctionTest {

    private CoverageTracker mockCoverageTracker;
    private Mutation<TestSuiteChromosome> mockMutation;
    private Crossover<TestSuiteChromosome> mockCrossover;

    @BeforeEach
    void setUp() {
        mockCoverageTracker = mock(CoverageTracker.class);
        mockMutation = Mutation.identity();
        mockCrossover = Crossover.identity();
    }

    @Test
    void testSizeFitnessFunction() {
        boolean[] testCases = {true, false, true, true};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double sizeWeight = 0.5;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

        double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
        double expectedFitness = (3.0 / 4.0) * sizeWeight; // 3 out of 4 test cases selected

        assertEquals(expectedFitness, fitness, 1e-6, "Size fitness calculation is incorrect!");
    }

    @Test
    void testCoverageFitnessFunction() {
        boolean[][] coverageMatrix = {
            {true, false, true}, // Test case 1 covers lines 1 and 3
            {false, true, false}, // Test case 2 covers line 2
            {true, true, true}   // Test case 3 covers all lines
        };
    
        try {
            when(mockCoverageTracker.getCoverageMatrix()).thenReturn(coverageMatrix);
    
            boolean[] testCases = {true, false, true}; // Select test cases 1 and 3
            TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);
    
            CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);
    
            double fitness = coverageFitnessFunction.applyAsDouble(chromosome);
            assertTrue(fitness > 0, "Coverage fitness should be greater than 0 for valid coverage.");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    

    @Test
    void testCoverageFitnessFunction_AllLinesCovered() {
        boolean[][] coverageMatrix = {
            {true, true, true},
            {true, true, true},
            {true, true, true}
        };
        boolean[] testCases = {true, true, true}; // All test cases selected
        CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);
        double fitness = coverageFitnessFunction.applyAsDouble(chromosome);

        assertEquals(1.0, fitness, "Fitness should be 1.0 when all lines are covered.");
    }

    @Test
    void testCoverageFitnessFunction_NoLinesCovered() {
        boolean[][] coverageMatrix = {
            {false, false, false},
            {false, false, false},
            {false, false, false}
        };
        boolean[] testCases = {false, false, false}; // No test cases selected
        CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);
        double fitness = coverageFitnessFunction.applyAsDouble(chromosome);

        assertEquals(0.0, fitness, "Fitness should be 0.0 when no lines are covered.");
    }

    @Test
    void testCoverageFitnessFunction_InvalidInputs() {
        boolean[][] invalidMatrix = {};
        assertThrows(IllegalArgumentException.class, () -> new CoverageFitnessFunction(invalidMatrix, 0.5));
    }

    @Test
    void testCoverageFitnessFunction_EmptyChromosome() {
        boolean[][] coverageMatrix = {
            {true, false, true},
            {false, true, false}
        };
        boolean[] testCases = {}; // Empty chromosome
        CoverageFitnessFunction coverageFitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        assertThrows(IllegalArgumentException.class, () -> coverageFitnessFunction.applyAsDouble(
            new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover)
        ));
    }
}
