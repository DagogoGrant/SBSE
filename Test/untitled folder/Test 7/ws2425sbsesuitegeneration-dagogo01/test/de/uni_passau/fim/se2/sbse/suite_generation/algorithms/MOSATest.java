package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Method;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;

class MOSATest {

    @Mock
    private Random mockRandom;

    @Mock
    private TestChromosomeGenerator mockGenerator;

    @Mock
    private StoppingCondition mockStoppingCondition;

    @Mock
    private TestChromosome mockTestChromosome;
    @Mock
    private TestChromosome mockTestChromosome1;
    @Mock
    private TestChromosome mockTestChromosome2;
    @Mock
    private TestChromosome mockTestChromosome3;

    @Mock
    private Map<TestChromosome, Map<Branch, Double>> mockFitnessMap;

    @Mock
    private Branch mockBranch;

    private MOSA mosa;
    private int populationSize = 10;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock target branches
        List<Branch> targetBranches = new ArrayList<>();
        targetBranches.add(mockBranch);

        // Initialize the MOSA instance
        mosa = new MOSA(populationSize, mockRandom, mockGenerator, targetBranches, mockStoppingCondition);
    }
    @Test
    public void testFindSolution() {
        when(mockStoppingCondition.searchMustStop())
            .thenReturn(false)
            .thenReturn(true);
    
        doNothing().when(mockStoppingCondition).notifySearchStarted();
        doNothing().when(mockStoppingCondition).notifyFitnessEvaluation();
    
        when(mockGenerator.get()).thenReturn(mockTestChromosome);
    
        when(mockFitnessMap.get(any(TestChromosome.class)))
            .thenReturn(Map.of(mockBranch, 0.5));
    
        TestChromosome mockOffspring1 = mock(TestChromosome.class);
        TestChromosome mockOffspring2 = mock(TestChromosome.class);
        Pair<TestChromosome> crossoverResult = new Pair<>(mockOffspring1, mockOffspring2);
        when(mockTestChromosome.crossover(any(TestChromosome.class))).thenReturn(crossoverResult);
    
        when(mockOffspring1.mutate()).thenReturn(mockOffspring1);
        when(mockOffspring2.mutate()).thenReturn(mockOffspring2);
    
        List<TestChromosome> solutions = mosa.findSolution();
    
        verify(mockStoppingCondition).notifySearchStarted();
        verify(mockStoppingCondition, times(2)).searchMustStop();
        verify(mockStoppingCondition).notifyFitnessEvaluation();
    
        assertEquals(10, solutions.size(), "Solutions should match the population size.");
    }
    

    @Test
    public void testStoppingCondition() {
        assertEquals(mockStoppingCondition, mosa.getStoppingCondition(),
            "The stopping condition should be correctly returned.");
    }

    @Test
    void testGenerateOffspring() {
        try {
            // Obtain the private method using reflection
            var generateOffspringMethod = MOSA.class.getDeclaredMethod(
                "generateOffspring",
                List.class,
                Map.class
            );
            generateOffspringMethod.setAccessible(true);

            // Mock random behavior for crossover
            when(mockRandom.nextDouble()).thenReturn(0.5); // Simulate crossover happening
            when(mockTestChromosome.crossover(mockTestChromosome)).thenReturn(new Pair<>(mockTestChromosome, mockTestChromosome));
            when(mockTestChromosome.mutate()).thenReturn(mockTestChromosome);

            // Mock population
            List<TestChromosome> mockPopulation = new ArrayList<>(Collections.nCopies(10, mockTestChromosome));

            // Mock fitness map
            Map<TestChromosome, Map<Branch, Double>> mockFitnessMap = Map.of(mockTestChromosome, Map.of(mockBranch, 0.5));

            // Invoke the private method
            @SuppressWarnings("unchecked")
            List<TestChromosome> offspring = (List<TestChromosome>) generateOffspringMethod.invoke(
                mosa, 
                mockPopulation, 
                mockFitnessMap
            );

            // Assertions
            assertEquals(mockPopulation.size(), offspring.size(), 
                "Offspring population size should match parent size.");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error invoking private method: " + e.getMessage(), e);
        }
    }

    @Test
    void testCalculateSubvectorDensity() throws Exception {
        List<TestChromosome> front = new ArrayList<>();
        front.add(mockTestChromosome1);
        front.add(mockTestChromosome2);
        front.add(mockTestChromosome3);
    
        when(mockFitnessMap.get(any(TestChromosome.class)))
            .thenReturn(Map.of(mockBranch, 0.5));
    
        Method method = MOSA.class.getDeclaredMethod("calculateSubvectorDensity", List.class, Map.class);
        method.setAccessible(true);
    
        method.invoke(mosa, front, mockFitnessMap);
    
        for (TestChromosome testCase : front) {
            assertTrue(testCase.getDensity() >= 0 && testCase.getDensity() <= 1);
        }
    }
    
    @Test
    void testCalculateSubvectorDensityWithSingleElement() throws Exception {
        List<TestChromosome> front = new ArrayList<>();
        front.add(mockTestChromosome1);
    
        when(mockFitnessMap.get(any(TestChromosome.class)))
            .thenReturn(Map.of(mockBranch, 0.5));
    
        Method method = MOSA.class.getDeclaredMethod("calculateSubvectorDensity", List.class, Map.class);
        method.setAccessible(true);
    
        method.invoke(mosa, front, mockFitnessMap);
    
        verify(mockTestChromosome1).setDensity(Double.POSITIVE_INFINITY);
    }
    
    
    @Test
void testCalculateSubvectorDensityEmptyFront() throws Exception {
    List<TestChromosome> front = new ArrayList<>();

    Method method = MOSA.class.getDeclaredMethod("calculateSubvectorDensity", List.class, Map.class);
    method.setAccessible(true);

    method.invoke(mosa, front, mockFitnessMap);

    verifyNoInteractions(mockTestChromosome1, mockTestChromosome2, mockTestChromosome3);
}

    
}
