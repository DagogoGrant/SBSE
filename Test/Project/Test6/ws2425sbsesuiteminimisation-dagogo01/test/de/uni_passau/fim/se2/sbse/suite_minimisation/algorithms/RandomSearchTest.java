package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomSearchTest {

    private TestSuiteChromosomeGenerator generator;
    private FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private StoppingCondition stoppingCondition;
    private RandomSearch randomSearch;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        generator = mock(TestSuiteChromosomeGenerator.class);
        sizeFitnessFunction = mock(FitnessFunction.class);
        coverageFitnessFunction = mock(FitnessFunction.class);
        stoppingCondition = mock(StoppingCondition.class);

        // Initialize RandomSearch
        randomSearch = new RandomSearch(generator, sizeFitnessFunction, coverageFitnessFunction, stoppingCondition);
    }

    @Test
    void testFindSolutionNormalExecution() {
        // Mock behavior
        when(stoppingCondition.searchMustStop()).thenReturn(false, false, true); // Stop after 2 iterations
        TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
        TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
        when(generator.get()).thenReturn(chromosome1, chromosome2);
        when(sizeFitnessFunction.applyAsDouble(chromosome1)).thenReturn(10.0);
        when(sizeFitnessFunction.applyAsDouble(chromosome2)).thenReturn(8.0);
        when(coverageFitnessFunction.applyAsDouble(chromosome1)).thenReturn(70.0);
        when(coverageFitnessFunction.applyAsDouble(chromosome2)).thenReturn(80.0);

        // Execute
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Verify
        assertNotNull(paretoFront);
        assertEquals(1, paretoFront.size()); // Only non-dominated solutions remain
        assertTrue(paretoFront.contains(chromosome2));
    }

    @Test
    void testFindSolutionEmptyParetoFront() {
        // Mock behavior
        when(stoppingCondition.searchMustStop()).thenReturn(true); // Stop immediately

        // Execute
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Verify
        assertNotNull(paretoFront);
        assertTrue(paretoFront.isEmpty()); // No solutions generated
    }

    @Test
    void testFindSolutionDominatedCandidates() {
        // Mock behavior
        when(stoppingCondition.searchMustStop()).thenReturn(false, false, true); // Stop after 2 iterations
        TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
        TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
        when(generator.get()).thenReturn(chromosome1, chromosome2);
        when(sizeFitnessFunction.applyAsDouble(chromosome1)).thenReturn(10.0);
        when(sizeFitnessFunction.applyAsDouble(chromosome2)).thenReturn(12.0); // Dominated by chromosome1
        when(coverageFitnessFunction.applyAsDouble(chromosome1)).thenReturn(80.0);
        when(coverageFitnessFunction.applyAsDouble(chromosome2)).thenReturn(70.0);

        // Execute
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Verify
        assertNotNull(paretoFront);
        assertEquals(1, paretoFront.size()); // Only chromosome1 should remain
        assertTrue(paretoFront.contains(chromosome1));
    }

    @Test
    void testFindSolutionNonDominatedCandidates() {
        // Mock behavior
        when(stoppingCondition.searchMustStop()).thenReturn(false, false, true); // Stop after 2 iterations
        TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
        TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
        when(generator.get()).thenReturn(chromosome1, chromosome2);
        when(sizeFitnessFunction.applyAsDouble(chromosome1)).thenReturn(10.0);
        when(sizeFitnessFunction.applyAsDouble(chromosome2)).thenReturn(10.0); // Non-dominated
        when(coverageFitnessFunction.applyAsDouble(chromosome1)).thenReturn(70.0);
        when(coverageFitnessFunction.applyAsDouble(chromosome2)).thenReturn(75.0); // Better coverage
    
        // Execute
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();
    
        // Debug Pareto front
        paretoFront.forEach(c -> System.out.printf("Chromosome in Pareto front: size = %f, coverage = %f%n",
                sizeFitnessFunction.applyAsDouble(c), coverageFitnessFunction.applyAsDouble(c)));
    
        // Verify
        assertNotNull(paretoFront);
        assertEquals(1, paretoFront.size()); // Only the non-dominated chromosome is retained
        assertTrue(paretoFront.contains(chromosome2)); // Dominant chromosome
        assertFalse(paretoFront.contains(chromosome1)); // Dominated chromosome
    }
    
    @Test
    void testFindSolutionRespectsStoppingCondition() {
        // Mock behavior
        when(stoppingCondition.searchMustStop()).thenReturn(true); // Stop immediately

        // Execute
        randomSearch.findSolution();

        // Verify that stopping condition was checked
        verify(stoppingCondition, atLeastOnce()).searchMustStop();
    }
}
