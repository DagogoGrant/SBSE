package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BranchCoverageFitnessFunctionTest {

    private Set<Integer> targetBranchIds;
    private TestChromosome chromosomeMock;

    @BeforeEach
    void setUp() {
        targetBranchIds = Set.of(1, 2, 3);
        chromosomeMock = mock(TestChromosome.class);
    }

    @Test
    void testConstructorInitialization() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);
        assertNotNull(fitnessFunction);
    }

    @Test
    void testApplyAsDoubleWithCoveredBranchesMinimize() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);

        Map<Integer, Double> branchDistances = new HashMap<>();
        branchDistances.put(1, 0.5);
        branchDistances.put(2, 0.2);
        branchDistances.put(3, 0.0);

        when(chromosomeMock.call()).thenReturn(branchDistances);

        double fitness = fitnessFunction.applyAsDouble(chromosomeMock);

        double expectedFitness = (0.5 / 1.5 + 0.2 / 1.2 + 0.0 / 1.0) / 3;
        assertEquals(expectedFitness, fitness, 1e-6);
    }

    @Test
    void testApplyAsDoubleWithUncoveredBranchesMinimize() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);

        when(chromosomeMock.call()).thenReturn(new HashMap<>()); // No coverage

        double fitness = fitnessFunction.applyAsDouble(chromosomeMock);

        double expectedFitness = (1.0 / 2.0) * 3 / 3;  // All branches uncovered
        assertEquals(expectedFitness, fitness, 1e-6);
    }

    @Test
    void testApplyAsDoubleWithCoveredBranchesMaximize() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, false);

        Map<Integer, Double> branchDistances = new HashMap<>();
        branchDistances.put(1, 0.5);
        branchDistances.put(2, 0.2);
        branchDistances.put(3, 0.0);

        when(chromosomeMock.call()).thenReturn(branchDistances);

        double fitness = fitnessFunction.applyAsDouble(chromosomeMock);

        double expectedFitness = (1.0 - (0.5 / 1.5) + 1.0 - (0.2 / 1.2) + 1.0 - (0.0 / 1.0)) / 3;
        assertEquals(expectedFitness, fitness, 1e-6);
    }

    @Test
    void testApplyAsDoubleWithUncoveredBranchesMaximize() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, false);

        when(chromosomeMock.call()).thenReturn(new HashMap<>()); // No coverage

        double fitness = fitnessFunction.applyAsDouble(chromosomeMock);

        double expectedFitness = (1.0 - (1.0 / 2.0)) * 3 / 3;  // All branches uncovered
        assertEquals(expectedFitness, fitness, 1e-6);
    }

    @Test
    void testIsMinimizing() {
        BranchCoverageFitnessFunction minimizingFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);
        assertTrue(minimizingFunction.isMinimizing());

        BranchCoverageFitnessFunction maximizingFunction = new BranchCoverageFitnessFunction(targetBranchIds, false);
        assertFalse(maximizingFunction.isMinimizing());
    }

    @Test
    void testCalculateBranchFitnessWithCoverage() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);

        Map<Integer, Double> branchDistances = new HashMap<>();
        branchDistances.put(1, 0.5);
        branchDistances.put(2, 0.0);
        branchDistances.put(3, 0.8);

        when(chromosomeMock.call()).thenReturn(branchDistances);

        Map<Integer, Double> branchFitness = fitnessFunction.calculateBranchFitness(chromosomeMock);

        assertEquals(0.5 / 1.5, branchFitness.get(1), 1e-6);
        assertEquals(0.0, branchFitness.get(2), 1e-6);
        assertEquals(0.8 / 1.8, branchFitness.get(3), 1e-6);
    }

    @Test
    void testCalculateBranchFitnessWithUncoveredBranches() {
        BranchCoverageFitnessFunction fitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);

        when(chromosomeMock.call()).thenReturn(new HashMap<>()); // No coverage

        Map<Integer, Double> branchFitness = fitnessFunction.calculateBranchFitness(chromosomeMock);

        assertEquals(1.0 / 2.0, branchFitness.get(1), 1e-6);
        assertEquals(1.0 / 2.0, branchFitness.get(2), 1e-6);
        assertEquals(1.0 / 2.0, branchFitness.get(3), 1e-6);
    }
}
