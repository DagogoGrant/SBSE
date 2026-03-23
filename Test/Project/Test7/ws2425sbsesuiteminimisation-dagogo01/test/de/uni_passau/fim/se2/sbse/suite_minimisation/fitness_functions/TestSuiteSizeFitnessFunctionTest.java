package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.AlgorithmBuilder;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.stream.IntStream;

public class TestSuiteSizeFitnessFunctionTest {

    // Add the stopping condition
    private final StoppingCondition stoppingCondition = new StoppingCondition() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void testMakeTestSuiteSizeFitnessFunctionInBuilder() {
        // Create a valid coverage matrix
        boolean[][] coverageMatrix = {
            {true, false, true}, // Test case 1
            {false, true, false}, // Test case 2
            {true, true, false}   // Test case 3
        };
    
        // Create a TestSuiteChromosome
        TestSuiteChromosome chromosome = new TestSuiteChromosome(new boolean[]{true, false, true}, null, null);
    
        // Instantiate the builder with a valid coverage matrix
        AlgorithmBuilder builder = new AlgorithmBuilder(new Random(), stoppingCondition, coverageMatrix);
    
        // Get the fitness function
        TestSuiteSizeFitnessFunction<TestSuiteChromosome> fitnessFunction =
                (TestSuiteSizeFitnessFunction<TestSuiteChromosome>) builder.getSizeFF();
    
        // Expected normalized size
        double expectedSize = 2.0 / 3.0; // 2 active tests out of 3 total
    
        // Apply fitness function
        double fitness = fitnessFunction.applyAsDouble(chromosome);
    
        // Assert the normalized size
        assertEquals(expectedSize, fitness, 0.0001, "The fitness value should match the normalized size.");
    }
    

    @Test
    public void testSizeFitnessFunction_EmptyGenes() {
        // Create a TestSuiteChromosome with empty genes
        boolean[] genes = {};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);

        // Instantiate the fitness function
        MinimizingFitnessFunction<TestSuiteChromosome> fitnessFunction = new MinimizingFitnessFunction<>() {
            @Override
            public double applyAsDouble(TestSuiteChromosome chromosome) {
                boolean[] genes = chromosome.getGenes();
                int activeTests = 0;
                for (boolean gene : genes) {
                    if (gene) activeTests++;
                }
                return activeTests;
            }
        };

        // Apply fitness function
        double fitness = fitnessFunction.applyAsDouble(chromosome);

        // Assert that fitness is zero
        assertEquals(0.0, fitness, "Fitness value should be zero for empty genes.");
    }

    @Test
    public void testSizeFitnessFunction_ValidGenes() {
        // Create a TestSuiteChromosome
        boolean[] genes = {true, false, true, true};
        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);
    
        // Instantiate the fitness function
        TestSuiteSizeFitnessFunction<TestSuiteChromosome> fitnessFunction = new TestSuiteSizeFitnessFunction<>(genes.length);
    
        // Calculate expected normalized size
        double expectedSize = 3.0 / genes.length;
    
        // Apply fitness function
        double fitness = fitnessFunction.applyAsDouble(chromosome);
    
        // Assert the normalized size
        assertEquals(expectedSize, fitness, 0.0001, "The normalized size should match the expected value.");
    }
    


@Test
public void testSizeFitnessFunction() {
    // Define genes for the chromosome
    boolean[] genes = {true, true, false, true}; // 3 active genes out of 4 total

    // Create a TestSuiteChromosome
    TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, null, null);

    // Instantiate the fitness function
    TestSuiteSizeFitnessFunction<TestSuiteChromosome> fitnessFunction =
        new TestSuiteSizeFitnessFunction<>(genes.length);

    // Compute expected normalized size
    double expectedSize = 3.0 / 4.0;

    // Apply fitness function
    double fitness = fitnessFunction.applyAsDouble(chromosome);

    // Assert the calculated value matches the expected value
    assertEquals(expectedSize, fitness, 0.0001, "The normalized size should match the expected value.");
}


}
