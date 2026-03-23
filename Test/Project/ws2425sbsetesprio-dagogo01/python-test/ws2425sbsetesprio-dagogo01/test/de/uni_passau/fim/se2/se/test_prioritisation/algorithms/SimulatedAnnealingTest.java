package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimulatedAnnealingTest {

    @Test
    void testFindSolution() {
        Random random = new Random();
        TestOrderGenerator generator = new TestOrderGenerator(random, 5, null);
        APLC fitnessFunction = new APLC(new boolean[5][5]);
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(100);

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, 1000, 0.95);
        TestOrder solution = sa.findSolution();
        assertNotNull(solution);
    }
}
