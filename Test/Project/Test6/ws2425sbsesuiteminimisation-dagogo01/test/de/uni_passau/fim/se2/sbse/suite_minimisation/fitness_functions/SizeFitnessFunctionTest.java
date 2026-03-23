package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SizeFitnessFunctionTest {

       // Mock Mutation and Crossover objects
    private final Mutation<TestSuiteChromosome> mockMutation = c -> c; // No-op mutation
    private final Crossover<TestSuiteChromosome> mockCrossover = (p1, p2) -> Pair.of(p1, p2); // Return valid pair

    @Test
    void testNormalCase() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mockMutation, mockCrossover, 10);
        chromosome.setTestCases(Arrays.asList(1, 2, 3));
        SizeFitnessFunction fitnessFunction = new SizeFitnessFunction(10);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.3, fitness, 1e-12, "Fitness should be correctly calculated for normal case.");
    }

    @Test
    void testEmptyChromosome() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mockMutation, mockCrossover, 10);
        SizeFitnessFunction fitnessFunction = new SizeFitnessFunction(10);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.0, fitness, "Fitness should be 0.0 for an empty chromosome.");
    }

    @Test
    void testFullCoverage() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mockMutation, mockCrossover, 10);
        chromosome.setTestCases(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        SizeFitnessFunction fitnessFunction = new SizeFitnessFunction(10);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(1.0, fitness, "Fitness should be 1.0 for a full coverage chromosome.");
    }

    @Test
    void testInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> {
            SizeFitnessFunction fitnessFunction = new SizeFitnessFunction(0); // Invalid state
        }, "Should throw an exception for zero total test cases.");
    }
    
}
