package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverageFitnessFunctionTest {

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
    void testCoverageFitnessFunction_ValidInputs() {
        boolean[][] coverageMatrix = {
            {true, false, true}, // Test case 1 covers lines 1 and 3
            {false, true, false}, // Test case 2 covers line 2
            {true, true, true}   // Test case 3 covers all lines
        };

        boolean[] testCases = {true, false, true}; // Select test cases 1 and 3
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertTrue(fitness > 0, "Fitness should be greater than 0 for valid coverage.");
    }

    @Test
    void testCoverageFitnessFunction_AllLinesCovered() {
        boolean[][] coverageMatrix = {
            {true, true, true},
            {true, true, true},
            {true, true, true}
        };
        boolean[] testCases = {true, true, true}; // All test cases selected
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
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
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.0, fitness, "Fitness should be 0.0 when no lines are covered.");
    }

    @Test
    void testCoverageFitnessFunction_InvalidMatrix() {
        boolean[][] emptyMatrix = {};
        assertThrows(IllegalArgumentException.class, () -> new CoverageFitnessFunction(emptyMatrix, 0.5),
                "Empty coverage matrix should throw an exception.");
    }

    @Test
    void testCoverageFitnessFunction_InvalidWeight() {
        boolean[][] coverageMatrix = {{true}};
        assertThrows(IllegalArgumentException.class, () -> new CoverageFitnessFunction(coverageMatrix, -0.1),
                "Negative weight coefficient should throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> new CoverageFitnessFunction(coverageMatrix, 1.1),
                "Weight coefficient greater than 1.0 should throw an exception.");
    }

    @Test
    void testCoverageFitnessFunction_EmptyChromosome() {
        boolean[][] coverageMatrix = {
            {true, false, true},
            {false, true, false}
        };
        boolean[] testCases = {}; // Empty chromosome
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        assertThrows(IllegalArgumentException.class, () -> fitnessFunction.applyAsDouble(chromosome),
                "Empty chromosome should throw an exception.");
    }

    // @Test
    // void testCoverageFitnessFunction_WeightEffect() {
    //     boolean[][] coverageMatrix = {
    //         {true, false, true}, // Test case 1 covers lines 1 and 3
    //         {false, true, false}, // Test case 2 covers line 2
    //         {true, true, true}   // Test case 3 covers all lines
    //     };
    
    //     boolean[] testCases = {true, false, true}; // Select test cases 1 and 3
    //     TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);
    
    //     CoverageFitnessFunction fitnessFunctionLowWeight = new CoverageFitnessFunction(coverageMatrix, 0.1);
    //     CoverageFitnessFunction fitnessFunctionHighWeight = new CoverageFitnessFunction(coverageMatrix, 0.9);
    
    //     double fitnessLowWeight = fitnessFunctionLowWeight.applyAsDouble(chromosome);
    //     double fitnessHighWeight = fitnessFunctionHighWeight.applyAsDouble(chromosome);
    
    //     // Add debugging outputs
    //     System.out.println("Debugging testCoverageFitnessFunction_WeightEffect:");
    //     System.out.println("- Low weight fitness: " + fitnessLowWeight);
    //     System.out.println("- High weight fitness: " + fitnessHighWeight);
    
    //     // Assert higher weights amplify the fitness value
    //     assertTrue(fitnessHighWeight > fitnessLowWeight, 
    //                "Higher weight coefficient should result in higher fitness for the same chromosome.");
    // }
    
    

    @Test
    void testCoverageFitnessFunction_DebuggingOutput() {
        boolean[][] coverageMatrix = {
            {true, true, false},
            {false, true, true},
            {true, false, true}
        };
        boolean[] testCases = {true, true, false};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);

        double fitness = fitnessFunction.applyAsDouble(chromosome);

        assertNotNull(fitness, "Fitness calculation should not return null.");
        assertTrue(fitness > 0, "Fitness should be greater than 0 for valid inputs.");
    }
    @Test
void testCoverageFitnessFunction_AllTestCasesUnused() {
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false},
        {true, true, true}
    };
    boolean[] testCases = {false, false, false}; // No test cases selected
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix, 0.5);
    double fitness = fitnessFunction.applyAsDouble(chromosome);

    assertEquals(0.0, fitness, "Fitness should be 0 when no test cases are selected.");
}
// @Test
// void testCoverageFitnessFunction_BoundaryWeightEffect() {
//     boolean[][] coverageMatrix = {
//         {true, false, true}, // Test case 1 covers lines 1 and 3
//         {false, true, false}, // Test case 2 covers line 2
//         {true, true, true}   // Test case 3 covers all lines
//     };

//     boolean[] testCases = {true, false, true}; // Select test cases 1 and 3
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

//     CoverageFitnessFunction fitnessLowWeight = new CoverageFitnessFunction(coverageMatrix, 0.1);
//     CoverageFitnessFunction fitnessHighWeight = new CoverageFitnessFunction(coverageMatrix, 0.9);

//     double fitnessLow = fitnessLowWeight.applyAsDouble(chromosome);
//     double fitnessHigh = fitnessHighWeight.applyAsDouble(chromosome);

//     // Debugging output
//     System.out.println("Low Weight Fitness: " + fitnessLow);
//     System.out.println("High Weight Fitness: " + fitnessHigh);

//     assertTrue(fitnessHigh > fitnessLow, "Fitness should increase with higher weights.");
// }



@Test
void testCoverageFitnessFunction_IrregularCoverageMatrix() {
    boolean[][] irregularMatrix = {
        {true, false, true},
        {false, true},
        {true, true, true}
    };

    assertThrows(IllegalArgumentException.class,
        () -> new CoverageFitnessFunction(irregularMatrix, 0.5),
        "Irregular coverage matrix should throw an exception.");
}


}
