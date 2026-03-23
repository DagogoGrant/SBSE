package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.SizeFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    @Test
    void testHandlesNullCandidate() {
        // Simulate the generator returning null
        when(mockGenerator.get()).thenReturn(null);
        when(mockStoppingCondition.searchMustStop()).thenReturn(false, true);
    
        // Execute the algorithm
        List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();
    
        // Assert that the Pareto front is empty
        assertNotNull(paretoFront);
        assertTrue(paretoFront.isEmpty(), "Pareto front should be empty when generator returns null");
    }
    

@Test
void testSingleCandidateAddedToParetoFront() {
    // Mock a single candidate
    TestSuiteChromosome candidate = mock(TestSuiteChromosome.class);

    when(mockGenerator.get()).thenReturn(candidate);
    when(mockSizeFitnessFunction.applyAsDouble(candidate)).thenReturn(0.5);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate)).thenReturn(0.8);
    when(mockStoppingCondition.searchMustStop()).thenReturn(false, true);

    // Execute the algorithm
    List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

    // Assert that the Pareto front contains exactly one candidate
    assertEquals(1, paretoFront.size());
    assertTrue(paretoFront.contains(candidate));
}

@Test
void testDominatingCandidateReplacesExisting() {
    // Mock two candidates
    TestSuiteChromosome candidate1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome candidate2 = mock(TestSuiteChromosome.class);

    when(mockGenerator.get()).thenReturn(candidate1, candidate2);
    when(mockSizeFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.5);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.5);

    when(mockSizeFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.3);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.8); // Dominates candidate1

    when(mockStoppingCondition.searchMustStop()).thenReturn(false, false, true);

    // Execute the algorithm
    List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

    // Assert that the Pareto front contains only the dominating candidate
    assertEquals(1, paretoFront.size());
    assertTrue(paretoFront.contains(candidate2));
    assertFalse(paretoFront.contains(candidate1));
}

@Test
void testNoCandidateDominates() {
    // Mock two candidates
    TestSuiteChromosome candidate1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome candidate2 = mock(TestSuiteChromosome.class);

    when(mockGenerator.get()).thenReturn(candidate1, candidate2);
    when(mockSizeFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.4);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.6);

    when(mockSizeFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.5);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.7); // Neither dominates the other

    when(mockStoppingCondition.searchMustStop()).thenReturn(false, false, true);

    // Execute the algorithm
    List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

    // Assert that both candidates are in the Pareto front
    assertEquals(2, paretoFront.size());
    assertTrue(paretoFront.contains(candidate1));
    assertTrue(paretoFront.contains(candidate2));
}

@Test
void testMultipleCandidatesAddedToParetoFront() {
    // Mock three candidates
    TestSuiteChromosome candidate1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome candidate2 = mock(TestSuiteChromosome.class);

    when(mockGenerator.get()).thenReturn(candidate1, candidate2);
    when(mockSizeFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.5);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate1)).thenReturn(0.5);

    when(mockSizeFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.6);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate2)).thenReturn(0.7); // Non-dominating

    when(mockStoppingCondition.searchMustStop()).thenReturn(false, false, true);

    // Execute the algorithm
    List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

    // Assert that both candidates are in the Pareto front
    assertNotNull(paretoFront);
    assertEquals(2, paretoFront.size(), "Pareto front should contain both non-dominating candidates");
    assertTrue(paretoFront.contains(candidate1), "Pareto front should contain candidate1");
    assertTrue(paretoFront.contains(candidate2), "Pareto front should contain candidate2");
}


@Test
void testStoppingConditionNotifiesProperly() {
    // Mock a single candidate
    TestSuiteChromosome candidate = mock(TestSuiteChromosome.class);

    when(mockGenerator.get()).thenReturn(candidate);
    when(mockSizeFitnessFunction.applyAsDouble(candidate)).thenReturn(0.5);
    when(mockCoverageFitnessFunction.applyAsDouble(candidate)).thenReturn(0.8);
    when(mockStoppingCondition.searchMustStop()).thenReturn(false, true);

    // Execute the algorithm
    List<TestSuiteChromosome> paretoFront = randomSearch.findSolution();

    // Verify that the stopping condition was notified of fitness evaluations
    verify(mockStoppingCondition, atLeastOnce()).notifyFitnessEvaluation();

    // Assert that the Pareto front contains exactly one candidate
    assertEquals(1, paretoFront.size());
    assertTrue(paretoFront.contains(candidate));
}


}
