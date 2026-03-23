package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.utils.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RandomWalkTest {

    private RandomWalk<TestOrder> randomWalk;

    @BeforeEach
    void setUp() {
        int maxEvaluations = 100;
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        Random random = Randomness.random();

        // Use ShiftToBeginningMutation for mutation, which is what TestOrder requires
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        int numTestCases = 5;  // Ensure that the number of test cases matches the coverage matrix rows
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, numTestCases);

        // Corrected coverage matrix size to match number of test cases
        boolean[][] coverageMatrix = {
            {true, false, true, false, true},
            {false, true, true, false, true},
            {true, true, false, true, false},
            {false, false, true, true, true},
            {true, false, false, true, true}
        };

        APLC fitnessFunction = new APLC(coverageMatrix);
        randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);
    }

    @Test
    void testFindSolution() {
        TestOrder solution = randomWalk.findSolution();
        assertNotNull(solution, "The solution should not be null.");
    }
}
