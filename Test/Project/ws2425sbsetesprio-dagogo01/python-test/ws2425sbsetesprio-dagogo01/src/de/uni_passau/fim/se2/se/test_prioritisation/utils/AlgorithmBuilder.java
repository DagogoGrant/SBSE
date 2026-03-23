package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import de.uni_passau.fim.se2.se.test_prioritisation.algorithms.*;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.TournamentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.crossover.OrderCrossover;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.CustomStoppingCondition;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.TestOrderFitnessFunction;

import java.util.Random;

/**
 * Builds and configures various search algorithms.
 */
public class AlgorithmBuilder {

    public static SearchAlgorithm<TestOrder> build(SearchAlgorithmType type,
                                                    int numTestCases,
                                                    boolean[][] adjacencyMatrix,
                                                    Random random) {
        StoppingCondition stoppingCondition = createStoppingCondition();
        FitnessFunction<TestOrder> fitnessFunction = createFitnessFunction(adjacencyMatrix);

        switch (type) {
            case RANDOM_SEARCH:
                return createRandomSearch(stoppingCondition, numTestCases, fitnessFunction, random);
            case SIMULATED_ANNEALING:
                return createSimulatedAnnealing(stoppingCondition, numTestCases, fitnessFunction, random, 100.0, 0.95);
            case SIMPLE_GENETIC_ALGORITHM:
                return createSimpleGeneticAlgorithm(stoppingCondition, numTestCases, fitnessFunction, random, 5);
            default:
                throw new IllegalArgumentException("Unsupported search algorithm type: " + type);
        }
    }

    private static StoppingCondition createStoppingCondition() {
        return new CustomStoppingCondition();
    }

    private static FitnessFunction<TestOrder> createFitnessFunction(boolean[][] adjacencyMatrix) {
        return new TestOrderFitnessFunction(adjacencyMatrix);
    }

    private static RandomSearch<TestOrder> createRandomSearch(StoppingCondition stoppingCondition,
                                                              int numTestCases,
                                                              FitnessFunction<TestOrder> fitnessFunction,
                                                              Random random) {
        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, null); // Default mutation applied
        return new RandomSearch<>(stoppingCondition, generator, fitnessFunction);
    }

    private static SimulatedAnnealing<TestOrder> createSimulatedAnnealing(StoppingCondition stoppingCondition,
                                                                          int numTestCases,
                                                                          FitnessFunction<TestOrder> fitnessFunction,
                                                                          Random random,
                                                                          double initialTemperature,
                                                                          double coolingRate) {
        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, null); // Default mutation applied
        return new SimulatedAnnealing<>(stoppingCondition, generator, fitnessFunction, initialTemperature, coolingRate);
    }

    private static SimpleGeneticAlgorithm<TestOrder> createSimpleGeneticAlgorithm(StoppingCondition stoppingCondition,
                                                                                  int numTestCases,
                                                                                  FitnessFunction<TestOrder> fitnessFunction,
                                                                                  Random random,
                                                                                  int tournamentSize) {
        TestOrderGenerator generator = new TestOrderGenerator(random, numTestCases, null); // Default mutation applied
        TournamentSelection<TestOrder> parentSelection = new TournamentSelection<>(random, tournamentSize);
        OrderCrossover crossover = new OrderCrossover();
        return new SimpleGeneticAlgorithm<>(stoppingCondition, generator, fitnessFunction, parentSelection, crossover);
    }
}
