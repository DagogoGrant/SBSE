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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RandomWalkTest {

    private RandomWalk<TestOrder> randomWalk;
    private MaxFitnessEvaluations stoppingCondition;
    private TestOrderGenerator generator;
    private APLC fitnessFunction;
    private Random random;

    @BeforeEach
    void setUp() {
        int maxEvaluations = 100;
        stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        random = Randomness.random();

        // Use ShiftToBeginningMutation for mutation, which is what TestOrder requires
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        int numTestCases = 5;  // Ensure that the number of test cases matches the coverage matrix rows
        generator = new TestOrderGenerator(random, mutation, numTestCases);

        // Corrected coverage matrix size to match number of test cases
        boolean[][] coverageMatrix = {
            {true, false, true, false, true},
            {false, true, true, false, true},
            {true, true, false, true, false},
            {false, false, true, true, true},
            {true, false, false, true, true}
        };

        fitnessFunction = new APLC(coverageMatrix);
        randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);
    }

    @Test
    void testFindSolution() {
        TestOrder solution = randomWalk.findSolution();
        assertNotNull(solution, "The solution should not be null.");
    }

    @Test
    void testConstructorWithNullStoppingCondition() {
        assertThrows(NullPointerException.class, () -> new RandomWalk<>(null, generator, fitnessFunction));
    }

    @Test
    void testConstructorWithNullEncodingGenerator() {
        assertThrows(NullPointerException.class, () -> new RandomWalk<>(stoppingCondition, null, fitnessFunction));
    }

    @Test
    void testConstructorWithNullFitnessFunction() {
        assertThrows(NullPointerException.class, () -> new RandomWalk<>(stoppingCondition, generator, null));
    }

    @Test
    void testStoppingConditionReached() {
        // Mock stopping condition to immediately stop
        MaxFitnessEvaluations mockStoppingCondition = mock(MaxFitnessEvaluations.class);
        when(mockStoppingCondition.searchMustStop()).thenReturn(true);

        RandomWalk<TestOrder> walkWithMockCondition = new RandomWalk<>(mockStoppingCondition, generator, fitnessFunction);
        TestOrder solution = walkWithMockCondition.findSolution();

        assertNotNull(solution, "The solution should not be null even if stopping condition is immediately met.");
        verify(mockStoppingCondition, times(1)).notifySearchStarted();
    }

    @Test
    void testGetStoppingCondition() {
        assertEquals(stoppingCondition, randomWalk.getStoppingCondition(), "The stopping condition should match the one provided in the constructor.");
    }
}
