package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RandomWalkTest {

    @Test
    void testFindSolution() {
        // Initialize random number generator
        Random random = new Random();
        
        // Define a coverage matrix for the APLC fitness function
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };

        // Create a mutation instance
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);

        // Instantiate TestOrderGenerator
        TestOrderGenerator generator = new TestOrderGenerator(random, 3, mutation);

        // Define the fitness function
        FitnessFunction<TestOrder> fitnessFunction = new APLC(coverageMatrix);

        // Define the stopping condition
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(100);

        // Instantiate the RandomWalk algorithm
        RandomWalk<TestOrder> randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);

        // Perform the search
        TestOrder solution = randomWalk.findSolution();

        // Assert that the solution is not null
        assertNotNull(solution);
    }
}
