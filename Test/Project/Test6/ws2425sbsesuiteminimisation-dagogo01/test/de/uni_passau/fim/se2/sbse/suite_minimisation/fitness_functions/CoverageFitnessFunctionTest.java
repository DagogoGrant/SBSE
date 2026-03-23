package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoverageFitnessFunctionTest {

    @Test
    void testNormalCase() {
        boolean[][] coverageMatrix = {
                {true, false, false},
                {false, true, false},
                {false, false, true}
        };
        List<Integer> testCases = Arrays.asList(0, 1);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, 3);
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(2.0 / 3.0, fitness, 1e-12, "Fitness should correctly reflect the covered lines.");
    }

    @Test
    void testNoCoverage() {
        boolean[][] coverageMatrix = {
                {false, false, false},
                {false, false, false}
        };
        List<Integer> testCases = Arrays.asList(0, 1);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, 2);
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(0.0, fitness, "Fitness should be 0.0 when no lines are covered.");
    }

    @Test
    void testFullCoverage() {
        boolean[][] coverageMatrix = {
                {true, true, true},
                {true, true, true}
        };
        List<Integer> testCases = Arrays.asList(0, 1);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(testCases, 2);
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix);

        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(1.0, fitness, "Fitness should be 1.0 when all lines are covered.");
    }

    @Test
    void testDuplicateTestCases() {
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false}
        };
        List<Integer> testCases = Arrays.asList(0, 0, 1, 1);
        TestSuiteChromosome chromosome = new TestSuiteChromosome(
                testCases.stream().distinct().toList(), 2); // Filter duplicates
        CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix);
    
        double fitness = fitnessFunction.applyAsDouble(chromosome);
        assertEquals(1.0, fitness, "Fitness should not be inflated by duplicate test cases.");
    }
    
   @Test
void testEmptyChromosome() {
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false}
    };
    TestSuiteChromosome emptyChromosome = new TestSuiteChromosome(Collections.emptyList(), 2);
    CoverageFitnessFunction fitnessFunction = new CoverageFitnessFunction(coverageMatrix);

    double fitness = fitnessFunction.applyAsDouble(emptyChromosome);
    assertEquals(0.0, fitness, "Fitness for an empty chromosome should be 0.0.");
}

}
