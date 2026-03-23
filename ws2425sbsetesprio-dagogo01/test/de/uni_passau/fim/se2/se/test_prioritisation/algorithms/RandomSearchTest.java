package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomSearchTest {

    private RandomSearch<TestOrder> randomSearch;
    private MaxFitnessEvaluations stoppingCondition;
    private TestOrderGenerator generator;
    private APLC fitnessFunction;
    private Random random;

    @BeforeEach
    void setUp() {
        int maxEvaluations = 10;
        stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        random = new Random(42);
        generator = new TestOrderGenerator(random, new TestMutation(), 3);
        fitnessFunction = new APLC(new boolean[][]{
                {true, false, true},
                {false, true, false},
                {true, true, false}
        });
        randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
    }

    @Test
    void testFindSolution() {
        TestOrder bestSolution = randomSearch.findSolution();
        assertNotNull(bestSolution, "The best solution should not be null");
        assertEquals(stoppingCondition, randomSearch.getStoppingCondition(), "Stopping condition should match");
    }

    @Test
    void testFindSolutionWithSingleEvaluation() {
        stoppingCondition = new MaxFitnessEvaluations(1);
        randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        TestOrder bestSolution = randomSearch.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null, even with a single evaluation");
    }

    @Test
    void testFindSolutionWithMultipleEvaluations() {
        stoppingCondition = new MaxFitnessEvaluations(100);
        randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        TestOrder bestSolution = randomSearch.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null after multiple evaluations");
    }

    @Test
    void testConstructorWithNullStoppingCondition() {
        assertThrows(NullPointerException.class, () -> new RandomSearch<>(null, generator, fitnessFunction));
    }

    @Test
    void testConstructorWithNullEncodingGenerator() {
        assertThrows(NullPointerException.class, () -> new RandomSearch<>(stoppingCondition, null, fitnessFunction));
    }

    @Test
    void testConstructorWithNullFitnessFunction() {
        assertThrows(NullPointerException.class, () -> new RandomSearch<>(stoppingCondition, generator, null));
    }

    @Test
    void testGetStoppingCondition() {
        assertEquals(stoppingCondition, randomSearch.getStoppingCondition(), "The stopping condition should match the one provided in the constructor.");
    }

    /**
     * A simple implementation of Mutation for testing purposes.
     */
    private static class TestMutation implements Mutation<TestOrder> {
        @Override
        public TestOrder apply(TestOrder encoding) {
            // For simplicity, just return the encoding unchanged
            return encoding;
        }
    }
}
