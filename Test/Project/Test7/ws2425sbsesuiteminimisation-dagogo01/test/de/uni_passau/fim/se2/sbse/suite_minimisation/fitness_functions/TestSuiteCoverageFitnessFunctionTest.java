package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.AlgorithmBuilder;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestSuiteCoverageFitnessFunctionTest {

    @Test
    public void testCoverageFitnessFunction_ValidChromosome() {
        // Define a coverage matrix
        boolean[][] coverageMatrix = {
            {true, false, true}, // Line 1
            {false, true, false}, // Line 2
            {true, true, false}  // Line 3
        };
    
        // Create a TestSuiteChromosome
        boolean[] genes = {true, false, true}; // Tests 1 and 3 are active
        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);
    
        // Instantiate the fitness function
        TestSuiteCoverageFitnessFunction<TestSuiteChromosome> fitnessFunction = new TestSuiteCoverageFitnessFunction<>(coverageMatrix);
    
        // Apply fitness function
        double fitness = fitnessFunction.applyAsDouble(chromosome);
    
        // Assert that fitness matches normalized coverage (4/3 becomes 1.0 as max coverage)
        assertEquals(1.0, fitness, 0.0001, "Fitness value should be capped at 1.0 when all lines are covered.");
    }
    

    @Test
    public void testCoverageFitnessFunction_NoCoverage() {
        // Define a coverage matrix
        boolean[][] coverageMatrix = {
            {false, false, false},
            {false, false, false},
            {false, false, false}
        };

        // Create a TestSuiteChromosome
        boolean[] genes = {false, false, false};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);

        // Instantiate the fitness function
        TestSuiteCoverageFitnessFunction<TestSuiteChromosome> fitnessFunction = new TestSuiteCoverageFitnessFunction<>(coverageMatrix);

        // Apply fitness function
        double fitness = fitnessFunction.applyAsDouble(chromosome);

        // Assert that fitness is zero
        assertEquals(0.0, fitness, "Fitness value should be zero when no lines are covered.");
    }

    @Test
    public void testCoverageFitnessFunction_NullChromosome() {
        // Define a coverage matrix
        boolean[][] coverageMatrix = {
            {true, false, true},
            {false, true, false},
            {true, true, false}
        };

        // Instantiate the fitness function
        TestSuiteCoverageFitnessFunction<TestSuiteChromosome> fitnessFunction = new TestSuiteCoverageFitnessFunction<>(coverageMatrix);

        // Assert that passing null throws NullPointerException
        assertThrows(NullPointerException.class, () -> fitnessFunction.applyAsDouble(null));
    }

    @Test
public void testCoverageFitnessFunction_EmptyGenes() {
    // Define a coverage matrix
    boolean[][] coverageMatrix = {
        {true, false, true},
        {false, true, false},
        {true, true, false}
    };

    // Create a TestSuiteChromosome with matching gene length but all inactive
    boolean[] genes = {false, false, false}; // Matches matrix rows
    TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);

    // Instantiate the fitness function
    TestSuiteCoverageFitnessFunction<TestSuiteChromosome> fitnessFunction = new TestSuiteCoverageFitnessFunction<>(coverageMatrix);

    // Apply fitness function
    double fitness = fitnessFunction.applyAsDouble(chromosome);

    // Assert that fitness is zero
    assertEquals(0.0, fitness, "Fitness value should be zero for inactive genes.");
}

@Test
public void testMakeTestSuiteCoverageFitnessFunctionInBuilder() {
    // Inline implementation of StoppingCondition
    StoppingCondition stoppingCondition = new StoppingCondition() {
        private int evaluations = 0;
        private final int maxEvaluations = 10;

        @Override
        public void notifySearchStarted() {
            evaluations = 0;
        }

        @Override
        public void notifyFitnessEvaluation() {
            evaluations++;
        }

        @Override
        public boolean searchMustStop() {
            return evaluations >= maxEvaluations;
        }

        @Override
        public double getProgress() {
            return (double) evaluations / maxEvaluations;
        }
    };

    // Create the builder with random, stopping condition, and coverage matrix
    AlgorithmBuilder builder = new AlgorithmBuilder(
        new Random(),
        stoppingCondition,
        new boolean[][]{
            {true, false, true},  // Test case 1
            {false, true, false}, // Test case 2
            {true, true, false}   // Test case 3
        }
    );

    // Retrieve the fitness function
    MaximizingFitnessFunction<TestSuiteChromosome> fitnessFunction =
        (MaximizingFitnessFunction<TestSuiteChromosome>) builder.getCoverageFF();

    // Create a chromosome
    TestSuiteChromosome chromosome = new TestSuiteChromosome(
        new boolean[]{true, false, true}, // Test cases 1 and 3 are active
        Mutation.identity(),
        Crossover.identity()
    );

    // Apply fitness function and assert
    double fitness = fitnessFunction.applyAsDouble(chromosome);

    // Assert the fitness value
    assertEquals(1.0, fitness, 0.0001, "The fitness value should match normalized coverage.");
}


}
