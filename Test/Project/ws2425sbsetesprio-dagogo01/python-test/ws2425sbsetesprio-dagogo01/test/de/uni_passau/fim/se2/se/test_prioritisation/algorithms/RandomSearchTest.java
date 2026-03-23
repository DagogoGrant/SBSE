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

class RandomSearchTest {

    @Test
    void testFindSolution() {
        System.out.println("Debug: Starting testFindSolution...");

        // Initialize components
        Random random = new Random();
        System.out.println("Debug: Random object created.");

        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };
        System.out.println("Debug: Coverage matrix initialized.");

        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(random);
        System.out.println("Debug: Mutation object created.");

        TestOrderGenerator generator = new TestOrderGenerator(random, 3, mutation);
        System.out.println("Debug: TestOrderGenerator created.");

        FitnessFunction<TestOrder> fitnessFunction = new APLC(coverageMatrix);
        System.out.println("Debug: Fitness function created.");

        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(100);
        System.out.println("Debug: Stopping condition initialized with max evaluations = 100.");

        // Instantiate and run the RandomSearch algorithm
        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
        System.out.println("Debug: RandomSearch object created.");

        TestOrder solution = randomSearch.findSolution();
        System.out.println("Debug: Solution found: " + solution);

        // Assert the solution is not null
        assertNotNull(solution, "The solution should not be null.");
    }
}
