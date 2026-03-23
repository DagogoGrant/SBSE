package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.crossover.OrderCrossover;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.TournamentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleGeneticAlgorithmTest {

    @Test
    void testFindSolution() {
        Random random = new Random();
        int[] fixedPositions = {0, 1, 2, 3, 4}; // Example fixed positions
        TestOrderGenerator generator = new TestOrderGenerator(random, fixedPositions.length, null, fixedPositions);
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(100);
        TournamentSelection<TestOrder> parentSelection = new TournamentSelection<>(random, 2);
        OrderCrossover crossover = new OrderCrossover();
        APLC fitnessFunction = new APLC(new boolean[5][5]);

        SimpleGeneticAlgorithm<TestOrder> ga = new SimpleGeneticAlgorithm<>(stoppingCondition, generator, fitnessFunction, parentSelection, crossover);
        TestOrder solution = ga.findSolution();
        assertNotNull(solution);
    }
}
