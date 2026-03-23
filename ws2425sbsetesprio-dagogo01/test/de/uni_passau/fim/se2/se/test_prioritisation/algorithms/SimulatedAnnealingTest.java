package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SimulatedAnnealing class.
 */
class SimulatedAnnealingTest {

    // A simple encoding generator
    static class TestOrderGenerator implements EncodingGenerator<TestOrder> {
        @Override
        public TestOrder get() {
            int[] initialPositions = {0, 1, 2, 3, 4}; // Example test suite order
            return new TestOrder(new ShiftToBeginningMutation(new Random()), initialPositions);
        }
    }

    // A simple fitness function used as an energy function
    static class TestEnergyFunction implements FitnessFunction<TestOrder> {
        @Override
        public double applyAsDouble(TestOrder encoding) {
            // Let's consider a simple energy function where higher sums imply worse solutions
            int sum = 0;
            for (int value : encoding.getPositions()) {
                sum += value;
            }
            return sum; // Higher sum means worse solution
        }

        @Override
        public double maximise(TestOrder encoding) {
            return applyAsDouble(encoding);
        }

        @Override
        public double minimise(TestOrder encoding) {
            return -applyAsDouble(encoding); // Minimisation: lower values are better
        }
    }

    // A simple stopping condition
    static class TestStoppingCondition implements StoppingCondition {
        private int evaluations = 0;
        private final int maxEvaluations;

        public TestStoppingCondition(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
        }

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
    }

    @Test
    void testFindSolution() {
        int maxEvaluations = 10;

        // Create test components
        TestStoppingCondition stoppingCondition = new TestStoppingCondition(maxEvaluations);
        TestOrderGenerator encodingGenerator = new TestOrderGenerator();
        TestEnergyFunction energyFunction = new TestEnergyFunction();
        Random random = new Random();

        // Initialize SimulatedAnnealing with the test components
        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition, encodingGenerator, energyFunction, 1, random);

        // Find the best solution
        TestOrder bestSolution = simulatedAnnealing.findSolution();

        // Verify the results
        assertNotNull(bestSolution, "The best solution should not be null.");
        assertTrue(bestSolution.size() > 0, "The best solution should contain at least one test case.");
    }

    @Test
    void testConstructorWithNullArguments() {
        TestOrderGenerator encodingGenerator = new TestOrderGenerator();
        TestEnergyFunction energyFunction = new TestEnergyFunction();
        Random random = new Random();

        assertThrows(IllegalArgumentException.class, () ->
                        new SimulatedAnnealing<>(null, encodingGenerator, energyFunction, 1, random),
                "StoppingCondition cannot be null.");
        assertThrows(IllegalArgumentException.class, () ->
                        new SimulatedAnnealing<>(new TestStoppingCondition(10), null, energyFunction, 1, random),
                "EncodingGenerator cannot be null.");
        assertThrows(IllegalArgumentException.class, () ->
                        new SimulatedAnnealing<>(new TestStoppingCondition(10), encodingGenerator, null, 1, random),
                "FitnessFunction cannot be null.");
        assertThrows(IllegalArgumentException.class, () ->
                        new SimulatedAnnealing<>(new TestStoppingCondition(10), encodingGenerator, energyFunction, 1, null),
                "Random cannot be null.");
    }

    @Test
    void testAcceptWorseSolution() {
        int maxEvaluations = 1;
        TestStoppingCondition stoppingCondition = new TestStoppingCondition(maxEvaluations);
        TestOrderGenerator encodingGenerator = new TestOrderGenerator();
        TestEnergyFunction energyFunction = new TestEnergyFunction();

        // Mock random to always accept worse solutions
        Random random = new Random() {
            @Override
            public double nextDouble() {
                return 0.0; // Always allow acceptance of worse solutions
            }
        };

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition, encodingGenerator, energyFunction, 1, random);

        TestOrder bestSolution = simulatedAnnealing.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null.");
    }

    @Test
    void testNoImprovement() {
        int maxEvaluations = 1;
        TestStoppingCondition stoppingCondition = new TestStoppingCondition(maxEvaluations);
        TestOrderGenerator encodingGenerator = new TestOrderGenerator();
        TestEnergyFunction energyFunction = new TestEnergyFunction();

        // Mock random to never accept worse solutions
        Random random = new Random() {
            @Override
            public double nextDouble() {
                return 1.0; // Never allow acceptance of worse solutions
            }
        };

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition, encodingGenerator, energyFunction, 1, random);

        TestOrder bestSolution = simulatedAnnealing.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null.");
    }
}
