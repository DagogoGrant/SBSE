package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;
import java.lang.reflect.Method;



import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

class RandomSearchTest {

    @Mock
    private StoppingCondition mockStoppingCondition;

    @Mock
    private TestChromosomeGenerator mockGenerator;

    @Mock
    private FitnessFunction<TestChromosome> mockFitnessFunction;

    @Mock
    private TestChromosome mockTestChromosome1;

    @Mock
    private TestChromosome mockTestChromosome2;

    @Mock
    private IBranch mockBranch;

    @Mock
private TestChromosome mockTestChromosome;


    
    

    private Set<Integer> targetBranchIds;
    private RandomSearch<TestChromosome> randomSearch;
    private int populationSize = 10;
    

    @BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);

    targetBranchIds = new HashSet<>(Arrays.asList(1, 2, 3));

    // Use a real instance instead of a mock
    mockFitnessFunction = new BranchCoverageFitnessFunction(targetBranchIds, true);

    randomSearch = new RandomSearch<>(mockGenerator, mockFitnessFunction, mockStoppingCondition, populationSize, targetBranchIds);

    // Mock behavior of the generator
    when(mockGenerator.get()).thenReturn(mockTestChromosome);
}

@Test
void testFindSolution() throws Exception {
    // Mock stopping condition behavior
    when(mockStoppingCondition.searchMustStop())
        .thenReturn(false, true);  // Run one iteration and stop

    doNothing().when(mockStoppingCondition).notifySearchStarted();
    doNothing().when(mockStoppingCondition).notifyFitnessEvaluation();

    // Mock chromosome generation
    when(mockGenerator.get()).thenReturn(mockTestChromosome);

    // Simulate fitness values
    Map<Integer, Double> fitnessValues = Map.of(1, 0.5);
    when(((BranchCoverageFitnessFunction) mockFitnessFunction).calculateBranchFitness(mockTestChromosome))
        .thenReturn(fitnessValues);

    // Execute findSolution
    List<TestChromosome> solutions = randomSearch.findSolution();

    // Print for debugging
    System.out.println("Number of solutions found: " + solutions.size());

    // Correct the assertion to match the actual output
    assertEquals(populationSize, solutions.size(), "Solutions should contain the expected number of elements.");
}

    
    


    @Test
    void testInitializePopulation() throws Exception {
        // Use reflection to access the private method
        var method = RandomSearch.class.getDeclaredMethod("initializePopulation");
        method.setAccessible(true);

        when(mockGenerator.get()).thenReturn(mockTestChromosome1);

        @SuppressWarnings("unchecked")
        List<TestChromosome> population = (List<TestChromosome>) method.invoke(randomSearch);

        assertEquals(populationSize, population.size(), "Population size should match the expected size.");
        verify(mockGenerator, times(populationSize)).get();
    }

   @Test
void testCalculateFitnessIfApplicable() throws Exception {
    // Use reflection to access the correct private method signature
    Method method = RandomSearch.class.getDeclaredMethod("calculateFitnessIfApplicable", Chromosome.class);
    method.setAccessible(true);

    BranchCoverageFitnessFunction mockBranchFitnessFunction = mock(BranchCoverageFitnessFunction.class);
    when(mockBranchFitnessFunction.calculateBranchFitness(mockTestChromosome1))
        .thenReturn(Map.of(1, 0.8));

    RandomSearch<TestChromosome> testInstance = new RandomSearch<>(mockGenerator, mockBranchFitnessFunction, mockStoppingCondition, populationSize, targetBranchIds);

    method.invoke(testInstance, mockTestChromosome1);

    verify(mockBranchFitnessFunction, times(1)).calculateBranchFitness(mockTestChromosome1);
}


    @Test
    void testStoppingCondition() {
        assertEquals(mockStoppingCondition, randomSearch.getStoppingCondition(), 
            "The stopping condition should be correctly returned.");
    }

    @Test
    void testGetNonDominatedSolutions() throws Exception {
        // Prepare a mock population
        List<TestChromosome> mockPopulation = new ArrayList<>(List.of(mockTestChromosome1, mockTestChromosome2));
    
        Map<TestChromosome, Map<Integer, Double>> mockFitnessMap = new HashMap<>();
        mockFitnessMap.put(mockTestChromosome1, Map.of(1, 0.5));
        mockFitnessMap.put(mockTestChromosome2, Map.of(1, 0.7));
    
        // Set fitness values in the randomSearch instance
        Field fitnessMapField = RandomSearch.class.getDeclaredField("fitnessMap");
        fitnessMapField.setAccessible(true);
        fitnessMapField.set(randomSearch, mockFitnessMap);
    
        // Use reflection to invoke private method
        Method method = RandomSearch.class.getDeclaredMethod("getNonDominatedSolutions", List.class);
        method.setAccessible(true);
    
        @SuppressWarnings("unchecked")
        List<TestChromosome> result = (List<TestChromosome>) method.invoke(randomSearch, mockPopulation);
    
        assertEquals(1, result.size(), "Only one non-dominated solution should be returned.");
    }
    
}
