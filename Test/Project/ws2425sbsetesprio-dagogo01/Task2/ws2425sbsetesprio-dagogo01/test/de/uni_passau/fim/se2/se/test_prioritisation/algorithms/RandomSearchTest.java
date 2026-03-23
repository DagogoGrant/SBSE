package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomSearchTest {

    @Test
    void testFindSolution() {
        int maxEvaluations = 10;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        TestOrder bestSolution = randomSearch.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null");
        assertEquals(stoppingCondition, randomSearch.getStoppingCondition(), "Stopping condition should match");
    }

    @Test
    void testFindSolutionWithSingleEvaluation() {
        int maxEvaluations = 1;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        TestOrder bestSolution = randomSearch.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null, even with a single evaluation");
    }

    @Test
    void testFindSolutionWithMultipleEvaluations() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        APLC fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        Random random = new Random(42);
        TestOrderGenerator generator = new TestOrderGenerator(random, new TestMutation(), 3);

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        TestOrder bestSolution = randomSearch.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null after multiple evaluations");
    }

    /**
     * A simple implementation of Mutation for testing purposes.
     */
    private static class TestMutation implements de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation<TestOrder> {
        @Override
        public TestOrder apply(TestOrder encoding) {
            return encoding;
        }
    }
}
