package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.SizeFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RandomSearchTest {

    private TestSuiteChromosomeGenerator mockGenerator;
    private SizeFitnessFunction mockSizeFitnessFunction;
    private CoverageFitnessFunction mockCoverageFitnessFunction;
    private StoppingCondition mockStoppingCondition;
    private RandomSearch randomSearch;

    @BeforeEach
    void setUp() {
        mockGenerator = mock(TestSuiteChromosomeGenerator.class);
        mockSizeFitnessFunction = mock(SizeFitnessFunction.class);
        mockCoverageFitnessFunction = mock(CoverageFitnessFunction.class);
        mockStoppingCondition = mock(StoppingCondition.class);

        randomSearch = new RandomSearch(
            mockGenerator,
            mockSizeFitnessFunction,
            mockCoverageFitnessFunction,
            mockStoppingCondition
        );
    }

    @Test
    void testFindSolutionStopsWhenConditionMet() {
        // Set up mock behavior
        when(mockStoppingCondition.searchMustStop()).thenReturn(false, false, true); // Stops after 3 iterations
        when(mockSizeFitnessFunction.applyAsDouble(any())).thenReturn(0.5);
        when(mockCoverageFitnessFunction.applyAsDouble(any())).thenReturn(0.8);
        when(mockGenerator.get()).thenReturn(mock(TestSuiteChromosome.class));

        // Execute the algorithm
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Verify the stopping condition was checked
        verify(mockStoppingCondition, atLeast(3)).searchMustStop();

        // Verify the generator and fitness functions were called
        verify(mockGenerator, atLeast(2)).get();
        verify(mockSizeFitnessFunction, atLeast(2)).applyAsDouble(any());
        verify(mockCoverageFitnessFunction, atLeast(2)).applyAsDouble(any());

        // Assert that the Pareto front is not null
        assertNotNull(paretoFront);
    }


    @Test
    void testHandlesEmptyGeneratorOutput() {
        // Simulate the generator returning null
        when(mockGenerator.get()).thenReturn(null);
        when(mockStoppingCondition.searchMustStop()).thenReturn(true);

        // Execute the algorithm
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Assert the Pareto front remains empty
        assertTrue(paretoFront.isEmpty());
    }

    @Test
    void testHandlesDominatedSolutions() {
        // Mock three candidate solutions
        TestSuiteChromosome candidate1 = mock(TestSuiteChromosome.class);
        TestSuiteChromosome candidate2 = mock(TestSuiteChromosome.class);
        TestSuiteChromosome candidate3 = mock(TestSuiteChromosome.class);

        when(mockGenerator.get()).thenReturn(candidate1, candidate2, candidate3);
        when(mockSizeFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.4);
        when(mockCoverageFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.6);

        when(mockSizeFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.5);
        when(mockCoverageFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.5); // Dominated by candidate1

        when(mockSizeFitnessFunction.applyAsDouble(candidate3)).thenReturn(0.3);
        when(mockCoverageFitnessFunction.applyAsDouble(candidate3)).thenReturn(0.8); // Dominates candidate1

        when(mockStoppingCondition.searchMustStop()).thenReturn(false, false, false, true);

        // Execute the algorithm
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

        // Assert the Pareto front has the correct content
        assertEquals(1, paretoFront.size());
        assertTrue(paretoFront.contains(candidate3));
    }

}
