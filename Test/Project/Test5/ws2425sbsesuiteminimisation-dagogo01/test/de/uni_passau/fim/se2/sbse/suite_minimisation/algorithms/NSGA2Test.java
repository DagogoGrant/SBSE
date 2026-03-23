package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.SizeFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NSGA2Test {

    private NSGA2 nsga2;
    private TestSuiteChromosomeGenerator generator;
    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;
    private BinaryTournamentSelection<TestSuiteChromosome> selection;
    private SizeFitnessFunction sizeFitnessFunction;
    private CoverageFitnessFunction coverageFitnessFunction;
    private StoppingCondition stoppingCondition;

    @BeforeEach
    void setUp() {
        generator = mock(TestSuiteChromosomeGenerator.class);
        mutation = mock(Mutation.class);
        crossover = mock(Crossover.class);
        selection = mock(BinaryTournamentSelection.class);
        sizeFitnessFunction = mock(SizeFitnessFunction.class);
        coverageFitnessFunction = mock(CoverageFitnessFunction.class);
        stoppingCondition = mock(StoppingCondition.class);

        nsga2 = new NSGA2(
                stoppingCondition,
                mutation,
                crossover,
                selection,
                sizeFitnessFunction,
                coverageFitnessFunction,
                10, // population size
                generator
        );
    }

    @Test
    void testInitializationCreatesPopulation() {
        // Mock generator to return dummy chromosomes
        TestSuiteChromosome mockChromosome = mock(TestSuiteChromosome.class);
        when(generator.get()).thenReturn(mockChromosome);

        // Ensure a valid population is created
        List<TestSuiteChromosome> population = nsga2.initializePopulation();
        assertNotNull(population, "Population should not be null.");
        assertEquals(10, population.size(), "Population size should match the configured size.");
        verify(generator, times(10)).get();
    }

    
    @Test
void testInitializePopulationHandlesNullChromosome() {
    when(generator.get()).thenReturn(null);

    Exception exception = assertThrows(IllegalStateException.class, () -> nsga2.initializePopulation());
    assertEquals("Chromosome generator produced a null chromosome.", exception.getMessage());
}
@Test
void testGenerateOffspringHandlesNullChildrenFromCrossover() {
    TestSuiteChromosome parent1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome parent2 = mock(TestSuiteChromosome.class);

    when(selection.apply(any())).thenReturn(parent1, parent2);
    when(parent1.crossover(parent2)).thenReturn(null);

    Exception exception = assertThrows(IllegalStateException.class, 
        () -> nsga2.generateOffspring(new ArrayList<>()));

    assertEquals("Crossover produced null children.", exception.getMessage());
}

    
}
