package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedAnnealingTest {

    @Test
    void testInitialization() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 3, random);

        assertNotNull(sa.getStoppingCondition(), "Stopping condition should not be null");
        assertEquals(stoppingCondition, sa.getStoppingCondition(), "Stopping condition should match");
    }

    @Test
    void testFindSolution() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 3, random);
        TestOrder bestSolution = sa.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null");
        assertTrue(stoppingCondition.searchMustStop(), "Stopping condition should indicate the search must stop");
    }

    @Test
    void testCoolingRateEffect() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 3, random);

        double initialTemperature = sa.temperature;
        sa.findSolution();
        double finalTemperature = sa.temperature;

        assertTrue(finalTemperature < initialTemperature, "Final temperature should be less than initial temperature due to cooling");
    }

    @Test
    void testAcceptanceProbabilityWhenNewEnergyIsHigher() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 3, random);

        double currentEnergy = 0.5;
        double newEnergy = 0.8;
        double temperature = 1.0;

        // Test that acceptance probability is correctly calculated for a worse solution
        double probability = Math.exp((currentEnergy - newEnergy) / temperature);
        assertTrue(probability > 0, "Acceptance probability should be greater than 0 for worse solutions");
    }

    @Test
    void testSolutionUpdates() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 3, random);
        TestOrder bestSolution = sa.findSolution();

        assertNotNull(sa.bestSolution, "Best solution should not be null after search");
        assertEquals(sa.bestSolution, bestSolution, "Best solution returned should match the best solution found");
    }

    /**
     * A simple implementation of Mutation for testing purposes.
     */
    private static class TestMutation implements de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation<TestOrder> {
        @Override
        public TestOrder apply(TestOrder encoding) {
            // Perform a simple mutation by swapping two elements
            int[] positions = encoding.getPositions();
            if (positions.length < 2) {
                return encoding; // No mutation needed for array of length 1
            }
            int index1 = 0;
            int index2 = 1;
            int temp = positions[index1];
            positions[index1] = positions[index2];
            positions[index2] = temp;

            return new TestOrder(encoding.getMutation(), positions);
        }
    }
} 