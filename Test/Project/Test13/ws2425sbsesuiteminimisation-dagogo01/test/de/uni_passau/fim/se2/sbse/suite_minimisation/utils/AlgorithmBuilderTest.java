package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms.GeneticAlgorithm;
import de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms.NSGA2;
import de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms.RandomSearch;
import de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms.SearchAlgorithmType;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.MinimizingFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.MaximizingFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlgorithmBuilderTest {

    private AlgorithmBuilder builder;
    private boolean[][] mockCoverageMatrix;
    private Random mockRandom;
    private StoppingCondition mockStoppingCondition;

    @BeforeEach
    void setUp() {
        mockRandom = mock(Random.class);
        mockCoverageMatrix = new boolean[][]{{true, false}, {false, true}};
        mockStoppingCondition = mock(StoppingCondition.class);
        builder = new AlgorithmBuilder(mockRandom, mockStoppingCondition, mockCoverageMatrix);
    }

    @Test
    void testConstructorValidInputs() {
        assertNotNull(builder);
    }

    @Test
    void testConstructorInvalidCoverageMatrix() {
        assertThrows(IllegalArgumentException.class, () ->
            new AlgorithmBuilder(mockRandom, mockStoppingCondition, new boolean[][]{}));
    }

    @Test
    void testBuildRandomSearch() {
        GeneticAlgorithm<?> algorithm = builder.buildAlgorithm(SearchAlgorithmType.RANDOM_SEARCH);
        assertNotNull(algorithm);
        assertTrue(algorithm instanceof RandomSearch);
    }

    @Test
    void testBuildNSGA2() {
        GeneticAlgorithm<?> algorithm = builder.buildAlgorithm(SearchAlgorithmType.NSGA_II);
        assertNotNull(algorithm);
        assertTrue(algorithm instanceof NSGA2);
    }

    @Test
    void testSizeFitnessFunctionInitialization() {
        MinimizingFitnessFunction<?> sizeFF = builder.getSizeFF();
        assertNotNull(sizeFF);
    }

    @Test
    void testCoverageFitnessFunctionInitialization() {
        MaximizingFitnessFunction<?> coverageFF = builder.getCoverageFF();
        assertNotNull(coverageFF);
    }
}
