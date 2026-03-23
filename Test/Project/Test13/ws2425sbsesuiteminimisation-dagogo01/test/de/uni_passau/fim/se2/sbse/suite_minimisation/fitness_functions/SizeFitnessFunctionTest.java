package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SizeFitnessFunctionTest {

    private CoverageTracker mockCoverageTracker;
    private Mutation<TestSuiteChromosome> mockMutation;
    private Crossover<TestSuiteChromosome> mockCrossover;

    @BeforeEach
    void setUp() {
        mockCoverageTracker = mock(CoverageTracker.class);  // Mock CoverageTracker
        mockMutation = Mutation.identity();  // Identity mutation for testing
        mockCrossover = Crossover.identity();  // Identity crossover for testing
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
    void testSizeFitnessFunction_AllSelected() {
        boolean[] testCases = {true, true, true, true};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double sizeWeight = 0.8;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

        double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
        assertEquals(sizeWeight, fitness, "Fitness should equal weight when all test cases are selected.");
    }

    @Test
    void testSizeFitnessFunction_NoneSelected() {
        boolean[] testCases = {false, false, false, false};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double sizeWeight = 0.5;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

        double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.0, fitness, "Fitness should be 0 when no test cases are selected.");
    }

    @Test
    void testSizeFitnessFunction_InvalidWeight() {
        assertThrows(IllegalArgumentException.class, () -> new SizeFitnessFunction(-0.1),
                "Negative weight should throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> new SizeFitnessFunction(1.1),
                "Weight greater than 1.0 should throw an exception.");
    }

    @Test
    void testSizeFitnessFunction_MinimumWeight() {
        boolean[] testCases = {true, false, true};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double sizeWeight = 0.0;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

        double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.0, fitness, "Fitness should be 0 when weight is 0.");
    }

    @Test
    void testSizeFitnessFunction_MaximumWeight() {
        boolean[] testCases = {true, false, true};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

        double sizeWeight = 1.0;
        SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

        double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
        double expectedFitness = (2.0 / 3.0); // 2 out of 3 selected
        assertEquals(expectedFitness, fitness, "Fitness calculation is incorrect for maximum weight.");
    }
    @Test
void testSizeFitnessFunction_NullChromosome() {
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(0.5);

    assertThrows(IllegalArgumentException.class, () -> sizeFitnessFunction.applyAsDouble(null),
            "Null chromosome should throw an exception.");
}

@Test
void testSizeFitnessFunction_NullTestCases() {
    TestSuiteChromosome chromosome = mock(TestSuiteChromosome.class);
    when(chromosome.getTestCases()).thenReturn(null);

    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(0.5);

    assertThrows(IllegalArgumentException.class, () -> sizeFitnessFunction.applyAsDouble(chromosome),
            "Null test cases should throw an exception.");
}
@Test
void testSizeFitnessFunction_EmptyTestCases() {
    boolean[] testCases = {};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(0.5);

    assertThrows(IllegalArgumentException.class, () -> sizeFitnessFunction.applyAsDouble(chromosome),
            "Empty test cases should throw an exception.");
}
@Test
void testSizeFitnessFunction_SingleTestCase() {
    boolean[] testCases = {true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.7;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    assertEquals(sizeWeight, fitness, "Fitness should equal weight for a single selected test case.");
}
@Test
void testSizeFitnessFunction_FullWeightAllSelected() {
    boolean[] testCases = {true, true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 1.0;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    assertEquals(1.0, fitness, "Fitness should be 1.0 when weight is 1.0 and all test cases are selected.");
}
@Test
void testSizeFitnessFunction_MixedSelection() {
    boolean[] testCases = {true, false, true, false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.4;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    double expectedFitness = (2.0 / 4.0) * sizeWeight; // 2 out of 4 selected
    assertEquals(expectedFitness, fitness, "Fitness calculation is incorrect for mixed selection.");
}
// @Test
// void testSizeFitnessFunction_FitnessCapping() {
//     boolean[] testCases = {true, true, true, true};
//     TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

//     double sizeWeight = 1.5; // Higher than allowed weight
//     SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

//     double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
//     assertEquals(1.0, fitness, "Fitness should be capped at 1.0.");
// }
@Test
void testSizeFitnessFunction_InvalidWeightThrowsException() {
    boolean[] testCases = {true, true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    // Test invalid size weight (greater than 1.0)
    assertThrows(IllegalArgumentException.class, () -> new SizeFitnessFunction(1.5),
            "Weight greater than 1.0 should throw an exception.");

    // Test invalid size weight (less than 0.0)
    assertThrows(IllegalArgumentException.class, () -> new SizeFitnessFunction(-0.1),
            "Weight less than 0.0 should throw an exception.");
}
@Test
void testSizeFitnessFunction_WeightBoundaries() {
    boolean[] testCases = {true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    // Test with weight 0.0
    SizeFitnessFunction fitnessFunctionZeroWeight = new SizeFitnessFunction(0.0);
    double fitnessZeroWeight = fitnessFunctionZeroWeight.applyAsDouble(chromosome);
    assertEquals(0.0, fitnessZeroWeight, "Fitness should be 0.0 when weight is 0.0.");

    // Test with weight 1.0
    SizeFitnessFunction fitnessFunctionMaxWeight = new SizeFitnessFunction(1.0);
    double fitnessMaxWeight = fitnessFunctionMaxWeight.applyAsDouble(chromosome);
    assertEquals(1.0, fitnessMaxWeight, "Fitness should equal normalized size when weight is 1.0.");
}

@Test
void testSizeFitnessFunction_Capping() {
    boolean[] testCases = {true, true, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    SizeFitnessFunction fitnessFunction = new SizeFitnessFunction(1.0);
    double fitness = fitnessFunction.applyAsDouble(chromosome);
    assertEquals(1.0, fitness, "Fitness should be capped at 1.0.");
}
@Test
void testSizeFitnessFunction_SingleTestCaseUnselected() {
    boolean[] testCases = {false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.6;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    assertEquals(0.0, fitness, "Fitness should be 0.0 for a single unselected test case.");
}
@Test
void testSizeFitnessFunction_MaxWeightMixedTestCases() {
    boolean[] testCases = {true, false, true, true, false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 1.0;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    double expectedFitness = (3.0 / 5.0); // 3 out of 5 selected
    assertEquals(expectedFitness, fitness, "Fitness calculation is incorrect for mixed test cases with max weight.");
}
@Test
void testSizeFitnessFunction_IntermediateWeightNoneSelected() {
    boolean[] testCases = {false, false, false};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.7;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    assertEquals(0.0, fitness, "Fitness should be 0.0 when no test cases are selected.");
}
@Test
void testSizeFitnessFunction_AlternatingTestCases() {
    boolean[] testCases = {true, false, true, false, true};
    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.5;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    double expectedFitness = (3.0 / 5.0) * 0.5; // 3 out of 5 selected
    assertEquals(expectedFitness, fitness, "Fitness calculation is incorrect for alternating test cases.");
}
@Test
void testSizeFitnessFunction_InvalidTestCases() {
    TestSuiteChromosome chromosome = mock(TestSuiteChromosome.class);
    when(chromosome.getTestCases()).thenReturn(null);

    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(0.5);

    assertThrows(IllegalArgumentException.class, () -> sizeFitnessFunction.applyAsDouble(chromosome),
            "Null test cases should throw an exception.");
}
@Test
void testSizeFitnessFunction_BoundaryValues() {
    assertDoesNotThrow(() -> new SizeFitnessFunction(0.0), "Constructor should accept a weight of 0.0.");
    assertDoesNotThrow(() -> new SizeFitnessFunction(1.0), "Constructor should accept a weight of 1.0.");
}
@Test
void testSizeFitnessFunction_OverweightHandling() {
    // Expect an exception for invalid size weights greater than 1.0
    assertThrows(IllegalArgumentException.class, () -> new SizeFitnessFunction(1.5),
        "Constructor should throw an exception for weights greater than 1.0.");
}

@Test
void testSizeFitnessFunction_LargeChromosome() {
    boolean[] testCases = new boolean[1000];
    for (int i = 0; i < testCases.length; i++) {
        testCases[i] = i % 2 == 0; // Every alternate test case is selected
    }

    TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, mockCoverageTracker, mockMutation, mockCrossover);

    double sizeWeight = 0.8;
    SizeFitnessFunction sizeFitnessFunction = new SizeFitnessFunction(sizeWeight);

    double fitness = sizeFitnessFunction.applyAsDouble(chromosome);
    double expectedFitness = (500.0 / 1000.0) * sizeWeight; // 500 out of 1000 selected
    assertEquals(expectedFitness, fitness, "Fitness calculation is incorrect for large chromosome.");
}


}
