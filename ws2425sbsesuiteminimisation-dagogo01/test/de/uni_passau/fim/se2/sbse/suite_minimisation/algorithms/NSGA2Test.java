package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class NSGA2Test {

    private StoppingCondition mockStoppingCondition;
    private Mutation<TestSuiteChromosome> mockMutation;
    private Crossover<TestSuiteChromosome> mockCrossover;
    private BinaryTournamentSelection<TestSuiteChromosome> mockSelection;
    private FitnessFunction<TestSuiteChromosome> mockSizeFitnessFunction;
    private FitnessFunction<TestSuiteChromosome> mockCoverageFitnessFunction;
    private TestSuiteChromosomeGenerator mockGenerator;

    @BeforeEach
    void setUp() {
        mockStoppingCondition = mock(StoppingCondition.class);
        mockMutation = mock(Mutation.class);
        mockCrossover = mock(Crossover.class);
        mockSelection = mock(BinaryTournamentSelection.class);
        mockSizeFitnessFunction = mock(FitnessFunction.class);
        mockCoverageFitnessFunction = mock(FitnessFunction.class);
        mockGenerator = mock(TestSuiteChromosomeGenerator.class);
    }

    @Test
    void testConstructorWithInvalidPopulationSize() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
                mockSizeFitnessFunction, mockCoverageFitnessFunction, 0, mockGenerator)
        );
        assertEquals("Population size must be greater than zero.", exception.getMessage());
    }

    @Test
    void testConstructorWithNullGenerator() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
                mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, null)
        );
        assertEquals("Chromosome generator cannot be null.", exception.getMessage());
    }

    @Test
    void testFindSolutionHandlesEmptyPopulation() {
        when(mockGenerator.get()).thenReturn(null);

        NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
            mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

        assertThrows(IllegalStateException.class, algorithm::findSolution, "Should throw exception for empty population");
    }

    @Test
    void testFindSolutionValidatesOffspringGeneration() {
        TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);
        when(mockGenerator.get()).thenReturn(mockChromosome);
        when(mockStoppingCondition.searchMustStop()).thenReturn(false, true);

        when(mockSizeFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(0.5);
        when(mockCoverageFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(0.8);
        when(mockSelection.apply(anyList())).thenReturn(mockChromosome);
        when(mockChromosome.crossover(mockChromosome)).thenReturn(Pair.of(mockChromosome, mockChromosome));
        when(mockChromosome.mutate()).thenReturn(mockChromosome);

        NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
            mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

        List<TestSuiteChromosome> solution = algorithm.findSolution();

        assertNotNull(solution, "Solution should not be null.");
        assertFalse(solution.isEmpty(), "Solution should not be empty.");
        assertEquals(10, solution.size(), "Solution size should match population size.");
    }

    @Test
    void testCrowdingDistanceCalculationHandlesEmptyFront() {
        NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
            mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

        assertDoesNotThrow(() -> algorithm.calculateCrowdingDistances(Collections.singletonList(new ArrayList<>())),
            "Should handle empty front without exceptions");
    }

//     @Test
// void testCrowdingDistanceCalculation() {
//     // Arrange
//     TestSuiteChromosome mockChromosome1 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome mockChromosome2 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome mockChromosome3 = mock(TestSuiteChromosome.class);

//     when(mockChromosome1.getObjective(anyInt())).thenReturn(0.0);
//     when(mockChromosome2.getObjective(anyInt())).thenReturn(0.5);
//     when(mockChromosome3.getObjective(anyInt())).thenReturn(1.0);

//     List<TestSuiteChromosome> front = List.of(mockChromosome1, mockChromosome2, mockChromosome3);

//     NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
//             mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

//     // Act
//     algorithm.calculateCrowdingDistances(Collections.singletonList(front));

//     // Assert
//     verify(mockChromosome1, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(mockChromosome3, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(mockChromosome2, atLeastOnce()).setCrowdingDistance(anyDouble());
// }
    
    @Test
    void testNonDominatedSortingWithReflection() throws Exception {
        TestSuiteChromosome chromosome = mock(TestSuiteChromosome.class);
        NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
            mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

        var method = NSGA2.class.getDeclaredMethod("nonDominatedSorting", List.class);
        method.setAccessible(true);

        List<List<TestSuiteChromosome>> fronts = (List<List<TestSuiteChromosome>>) method.invoke(algorithm, List.of(chromosome));

        assertNotNull(fronts, "Fronts should not be null.");
        assertEquals(1, fronts.size(), "There should be one front.");
    }

@Test
void testCalculateCrowdingDistancesWithReflection() throws Exception {
    // Arrange
    TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome chromosome3 = mock(TestSuiteChromosome.class);

    when(chromosome1.getObjective(anyInt())).thenReturn(0.0);
    when(chromosome2.getObjective(anyInt())).thenReturn(0.5);
    when(chromosome3.getObjective(anyInt())).thenReturn(1.0);

    List<TestSuiteChromosome> front = List.of(chromosome1, chromosome2, chromosome3);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    // Use reflection to call the protected method
    var method = NSGA2.class.getDeclaredMethod("calculateCrowdingDistances", List.class);
    method.setAccessible(true);
    method.invoke(algorithm, Collections.singletonList(front));

    // Assert
    verify(chromosome1, times(2)).setCrowdingDistance(Double.POSITIVE_INFINITY);
    verify(chromosome3, times(2)).setCrowdingDistance(Double.POSITIVE_INFINITY);
    verify(chromosome2, atLeastOnce()).setCrowdingDistance(anyDouble());
}
// @Test
// void testCalculateCrowdingDistancesWithIdenticalObjectives() throws Exception {
//     // Arrange
//     TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome chromosome3 = mock(TestSuiteChromosome.class);

//     when(chromosome1.getObjective(anyInt())).thenReturn(0.5);
//     when(chromosome2.getObjective(anyInt())).thenReturn(0.5);
//     when(chromosome3.getObjective(anyInt())).thenReturn(0.5);

//     List<TestSuiteChromosome> front = List.of(chromosome1, chromosome2, chromosome3);

//     NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
//         mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

//     // Use reflection to call the protected method
//     var method = NSGA2.class.getDeclaredMethod("calculateCrowdingDistances", List.class);
//     method.setAccessible(true);

//     // Act
//     method.invoke(algorithm, Collections.singletonList(front));

//     // Assert
//     verify(chromosome1, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(chromosome3, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(chromosome2, atLeastOnce()).setCrowdingDistance(anyDouble());
// }

@Test
void testGenerateOffspringWithEmptyPopulation() throws Exception {
    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("generateOffspring", List.class);
    method.setAccessible(true);

    try {
        method.invoke(algorithm, Collections.emptyList());
        fail("Expected IllegalStateException for empty population during offspring generation.");
    } catch (InvocationTargetException e) {
        assertTrue(e.getCause() instanceof IllegalStateException, "Expected IllegalStateException as the root cause.");
    }
}

@Test
void testUpdatePopulationWithExactSize() throws Exception {
    TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("updatePopulation", List.class, List.class);
    method.setAccessible(true);

    List<TestSuiteChromosome> population = Collections.nCopies(10, mockChromosome);
    List<TestSuiteChromosome> offspring = Collections.emptyList();

    List<TestSuiteChromosome> updatedPopulation = (List<TestSuiteChromosome>) method.invoke(algorithm, population, offspring);

    assertNotNull(updatedPopulation, "Updated population should not be null.");
    assertEquals(10, updatedPopulation.size(), "Updated population size should match the original population size.");
}
@Test
void testUpdatePopulationWithExcessPopulation() throws Exception {
    TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("updatePopulation", List.class, List.class);
    method.setAccessible(true);

    List<TestSuiteChromosome> population = Collections.nCopies(10, mockChromosome);
    List<TestSuiteChromosome> offspring = Collections.nCopies(5, mockChromosome);

    List<TestSuiteChromosome> updatedPopulation = (List<TestSuiteChromosome>) method.invoke(algorithm, population, offspring);

    assertNotNull(updatedPopulation, "Updated population should not be null.");
    assertEquals(10, updatedPopulation.size(), "Updated population size should be reduced to the required population size.");
}
@Test
void testCalculateCrowdingDistancesWithTies() throws Exception {
    TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome chromosome3 = mock(TestSuiteChromosome.class);

    when(chromosome1.getObjective(anyInt())).thenReturn(0.1);
    when(chromosome2.getObjective(anyInt())).thenReturn(0.2);
    when(chromosome3.getObjective(anyInt())).thenReturn(0.3);

    List<TestSuiteChromosome> front = List.of(chromosome1, chromosome2, chromosome3);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("calculateCrowdingDistances", List.class);
    method.setAccessible(true);
    method.invoke(algorithm, Collections.singletonList(front));

    verify(chromosome2, atLeastOnce()).setCrowdingDistance(anyDouble());
}
@Test
void testInitializePopulationWithValidGenerator() throws Exception {
    TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);
    when(mockGenerator.get()).thenReturn(mockChromosome);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("initializePopulation");
    method.setAccessible(true);

    List<TestSuiteChromosome> population = (List<TestSuiteChromosome>) method.invoke(algorithm);

    assertNotNull(population, "Population should not be null.");
    assertEquals(10, population.size(), "Population size should match the initialized size.");
    assertTrue(population.stream().allMatch(chromosome -> chromosome == mockChromosome),
        "All chromosomes should match the mock generator's output.");
}
// @Test
// void testNonDominatedSortingWithMixedDominance() throws Exception {
//     TestSuiteChromosome dominatedChromosome = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome nonDominatedChromosome = mock(TestSuiteChromosome.class);

//     // Mock the dominates method
//     when(dominatedChromosome.dominates(nonDominatedChromosome)).thenReturn(false);
//     when(nonDominatedChromosome.dominates(dominatedChromosome)).thenReturn(true);

//     NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
//         mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

//     // Use reflection to invoke nonDominatedSorting
//     var method = NSGA2.class.getDeclaredMethod("nonDominatedSorting", List.class);
//     method.setAccessible(true);

//     @SuppressWarnings("unchecked")
//     List<List<TestSuiteChromosome>> fronts = (List<List<TestSuiteChromosome>>) method.invoke(algorithm,
//         List.of(dominatedChromosome, nonDominatedChromosome));

//     // Assertions
//     assertNotNull(fronts, "Fronts should not be null.");
//     assertEquals(2, fronts.size(), "There should be two fronts.");
//     assertTrue(fronts.get(0).contains(nonDominatedChromosome), "First front should contain the non-dominated chromosome.");
//     assertTrue(fronts.get(1).contains(dominatedChromosome), "Second front should contain the dominated chromosome.");
// }

@Test
void testUpdatePopulationWithOverflow() throws Exception {
    TestSuiteChromosome mockChromosome1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome mockChromosome2 = mock(TestSuiteChromosome.class);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 5, mockGenerator);

    var method = NSGA2.class.getDeclaredMethod("updatePopulation", List.class, List.class);
    method.setAccessible(true);

    List<TestSuiteChromosome> population = List.of(mockChromosome1, mockChromosome2);
    List<TestSuiteChromosome> offspring = List.of(mockChromosome1, mockChromosome2, mockChromosome1, mockChromosome2);

    List<TestSuiteChromosome> updatedPopulation = (List<TestSuiteChromosome>) method.invoke(algorithm, population, offspring);

    assertNotNull(updatedPopulation, "Updated population should not be null.");
    assertEquals(5, updatedPopulation.size(), "Updated population size should not exceed the limit.");
}
// @Test
// void testGenerateOffspringHandlesNullMutationAndCrossover() throws Exception {
//     TestSuiteChromosome parent = mock(TestSuiteChromosome.class);
//     Pair<TestSuiteChromosome> mockPair = Pair.of(null, null);

//     when(mockSelection.apply(anyList())).thenReturn(parent);
//     when(parent.crossover(parent)).thenReturn(mockPair);

//     NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
//         mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

//     var method = NSGA2.class.getDeclaredMethod("generateOffspring", List.class);
//     method.setAccessible(true);

//     List<TestSuiteChromosome> offspring = (List<TestSuiteChromosome>) method.invoke(algorithm, Collections.singletonList(parent));

//     assertNotNull(offspring, "Offspring should not be null.");
//     assertTrue(offspring.isEmpty(), "Offspring should remain empty if mutation or crossover results are null.");
// }
@Test
void testFindSolutionRespectsStoppingCondition() {
    when(mockGenerator.get()).thenReturn(mock(TestSuiteChromosome.class));
    when(mockStoppingCondition.searchMustStop()).thenReturn(true);

    NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
        mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

    List<TestSuiteChromosome> solution = algorithm.findSolution();

    assertNotNull(solution, "Solution should not be null.");
    assertEquals(10, solution.size(), "Solution size should match the initial population size.");
    verify(mockStoppingCondition, atLeastOnce()).searchMustStop();
}
// @Test
// void testCalculateCrowdingDistancesWithIdenticalObjectives() throws Exception {
//     TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);
//     TestSuiteChromosome chromosome3 = mock(TestSuiteChromosome.class);

//     when(chromosome1.getObjective(anyInt())).thenReturn(0.5);
//     when(chromosome2.getObjective(anyInt())).thenReturn(0.5);
//     when(chromosome3.getObjective(anyInt())).thenReturn(0.5);

//     List<TestSuiteChromosome> front = List.of(chromosome1, chromosome2, chromosome3);

//     NSGA2 algorithm = new NSGA2(mockStoppingCondition, mockMutation, mockCrossover, mockSelection,
//         mockSizeFitnessFunction, mockCoverageFitnessFunction, 10, mockGenerator);

//     var method = NSGA2.class.getDeclaredMethod("calculateCrowdingDistances", List.class);
//     method.setAccessible(true);

//     method.invoke(algorithm, Collections.singletonList(front));

//     verify(chromosome1, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(chromosome3, times(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
//     verify(chromosome2, never()).setCrowdingDistance(anyDouble());
// }


}
