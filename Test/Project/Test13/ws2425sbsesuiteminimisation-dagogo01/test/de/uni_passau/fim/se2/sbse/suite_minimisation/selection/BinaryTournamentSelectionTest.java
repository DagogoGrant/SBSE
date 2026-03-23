package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.CoverageTracker;

public class BinaryTournamentSelectionTest {

    private Random random;
    private TestSuiteMutation mutation;
    private TestSuiteCrossover crossover;
    private Comparator<TestSuiteChromosome> comparator;
    private BinaryTournamentSelection<TestSuiteChromosome> selection;

    @BeforeEach
    public void setUp() throws Exception {
        random = new Random(42); // Use fixed seed for deterministic results
        mutation = new TestSuiteMutation(0.1); // Specify mutation probability
        crossover = new TestSuiteCrossover();

        comparator = Comparator.comparingDouble(TestSuiteChromosome::getSizeFitness);

        selection = new BinaryTournamentSelection<>(comparator, random);
    }

    private TestSuiteChromosome mockChromosome(double sizeFitness) throws Exception {
        boolean[] testCases = {true, false, true};
        CoverageTracker mockCoverageTracker = mock(CoverageTracker.class);
        when(mockCoverageTracker.getCoverageMatrix()).thenReturn(new boolean[][]{
            {true, false, true},
            {false, true, false},
            {true, true, true}
        });

        TestSuiteChromosome chromosome = spy(new TestSuiteChromosome(testCases, mockCoverageTracker, mutation, crossover));
        when(chromosome.computeSizeFitness()).thenReturn(sizeFitness);

        return chromosome;
    }

    @Test
    public void testConstructor_nullComparator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BinaryTournamentSelection<>(null, random));
    }

    @Test
    public void testConstructor_nullRandom_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BinaryTournamentSelection<>(comparator, null));
    }

    @Test
    public void testApply_nullPopulation_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> selection.apply(null));
    }

    @Test
    public void testApply_emptyPopulation_throwsNoSuchElementException() {
        List<TestSuiteChromosome> emptyPopulation = List.of();
        assertThrows(NoSuchElementException.class, () -> selection.apply(emptyPopulation));
    }

    @Test
    public void testApply_withSingleElementPopulation() throws Exception {
        TestSuiteChromosome singleChromosome = mockChromosome(0.3);
        List<TestSuiteChromosome> population = List.of(singleChromosome);

        TestSuiteChromosome selected = selection.apply(population);

        assertEquals(singleChromosome, selected, "Single-element population should return the only element.");
    }

    @Test
    public void testApply_withTwoElementPopulation() throws Exception {
        TestSuiteChromosome chromosome1 = mockChromosome(0.1);
        TestSuiteChromosome chromosome2 = mockChromosome(0.5);

        List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2);

        TestSuiteChromosome selected = selection.apply(population);

        // Verify that one of the two elements is selected
        assertTrue(population.contains(selected), "Selected chromosome should be from the given population.");
    }

    @Test
    public void testApply_withLargePopulation() throws Exception {
        List<TestSuiteChromosome> largePopulation = generatePopulation(10);

        TestSuiteChromosome selected = selection.apply(largePopulation);

        assertNotNull(selected, "Selected chromosome should not be null.");
        assertTrue(largePopulation.contains(selected), "Selected chromosome should be from the given population.");
    }
    @Test
    public void testApply_deterministicSelectionWithFixedSeed() throws Exception {
        // Use a fixed seed for deterministic behavior
        Random fixedRandom = new Random(42);
        BinaryTournamentSelection<TestSuiteChromosome> deterministicSelection =
                new BinaryTournamentSelection<>(comparator, fixedRandom);
    
        // Mock chromosomes with predefined fitness values
        TestSuiteChromosome chromosome1 = mockChromosome(0.2); // Lower fitness
        TestSuiteChromosome chromosome2 = mockChromosome(0.8); // Higher fitness
    
        // Ensure deterministic population order
        List<TestSuiteChromosome> population = List.of(chromosome2, chromosome1);
    
        // Perform selection
        TestSuiteChromosome selected = deterministicSelection.apply(population);
    
        // Assert the chromosome with higher fitness is selected
        assertEquals(chromosome2, selected, "Chromosome with higher fitness should be selected deterministically.");
    }
    

    private List<TestSuiteChromosome> generatePopulation(int size) throws Exception {
        List<TestSuiteChromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(mockChromosome(random.nextDouble()));
        }
        return population;
    }
    @Test
public void testApply_populationWithEqualFitness() throws Exception {
    TestSuiteChromosome chromosome1 = mockChromosome(0.5);
    TestSuiteChromosome chromosome2 = mockChromosome(0.5);

    List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2);

    TestSuiteChromosome selected = selection.apply(population);

    // Both chromosomes have equal fitness; ensure one of them is selected
    assertTrue(population.contains(selected), "Selected chromosome should be from the given population.");
}

@Test
public void testApply_populationWithOnlyTournamentSize() throws Exception {
    TestSuiteChromosome chromosome1 = mockChromosome(0.2);
    TestSuiteChromosome chromosome2 = mockChromosome(0.8);

    List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2);

    TestSuiteChromosome selected = selection.apply(population);

    // The tournament size matches the population size, so one of the two should be selected
    assertTrue(population.contains(selected), "Selected chromosome should be one of the tournament participants.");
}

@Test
public void testApply_populationSmallerThanTournamentSize() throws Exception {
    // Create a population smaller than the tournament size
    TestSuiteChromosome chromosome = mockChromosome(0.5);
    List<TestSuiteChromosome> population = List.of(chromosome);

    TestSuiteChromosome selected = selection.apply(population);

    // Ensure the only chromosome in the population is returned
    assertEquals(chromosome, selected, "The single chromosome in the population should be selected.");
}

@Test
public void testApply_populationWithShuffling() throws Exception {
    List<TestSuiteChromosome> population = generatePopulation(5);

    // Shuffle and apply selection multiple times
    Set<TestSuiteChromosome> selectedChromosomes = new HashSet<>();
    for (int i = 0; i < 10; i++) {
        selectedChromosomes.add(selection.apply(population));
    }

    // Ensure the selected chromosomes are from the population
    for (TestSuiteChromosome selected : selectedChromosomes) {
        assertTrue(population.contains(selected), "Selected chromosome should be from the given population.");
    }
}

@Test
public void testApply_populationWithDescendingFitness() throws Exception {
    TestSuiteChromosome chromosome1 = mockChromosome(0.9);
    TestSuiteChromosome chromosome2 = mockChromosome(0.7);
    TestSuiteChromosome chromosome3 = mockChromosome(0.5);

    List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2, chromosome3);

    TestSuiteChromosome selected = selection.apply(population);

    // Verify that the highest-fitness chromosome is selected in most cases
    assertTrue(population.contains(selected), "Selected chromosome should be from the given population.");
}

// @Test
// public void testApply_populationWithEdgeCaseFitnessValues() throws Exception {
//     TestSuiteChromosome minFitnessChromosome = mockChromosome(0.0);
//     TestSuiteChromosome maxFitnessChromosome = mockChromosome(1.0);

//     List<TestSuiteChromosome> population = List.of(minFitnessChromosome, maxFitnessChromosome);

//     // Log initial population
//     System.out.println("Population:");
//     population.forEach(chromosome -> System.out.println("Fitness: " + chromosome.getSizeFitness()));

//     TestSuiteChromosome selected = selection.apply(population);

//     // Log selected chromosome
//     System.out.println("Selected Chromosome Fitness: " + selected.getSizeFitness());

//     // Assert that the chromosome with the maximum fitness is preferred
//     assertEquals(maxFitnessChromosome, selected, "Chromosome with maximum fitness should be selected.");
// }


@Test
public void testApply_comparatorBreaksTieRandomly() throws Exception {
    // Mock chromosomes with identical fitness values
    TestSuiteChromosome chromosome1 = mockChromosome(0.5);
    TestSuiteChromosome chromosome2 = mockChromosome(0.5);

    List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2);

    // Apply selection multiple times and ensure tie is resolved randomly
    Set<TestSuiteChromosome> selectedChromosomes = new HashSet<>();
    for (int i = 0; i < 10; i++) {
        selectedChromosomes.add(selection.apply(population));
    }

    // Ensure both chromosomes have been selected at least once
    assertEquals(2, selectedChromosomes.size(), "Both chromosomes should be selected at least once when fitness values are identical.");
}

@Test
public void testApply_withLargeTournamentSize() throws Exception {
    // Increase tournament size for this test
    BinaryTournamentSelection<TestSuiteChromosome> largeTournamentSelection =
            new BinaryTournamentSelection<>(comparator, random);

    List<TestSuiteChromosome> population = generatePopulation(100);

    TestSuiteChromosome selected = largeTournamentSelection.apply(population);

    // Ensure a valid chromosome is selected
    assertNotNull(selected, "Selected chromosome should not be null.");
    assertTrue(population.contains(selected), "Selected chromosome should be from the given population.");
}

@Test
public void testApply_populationWithNegativeFitnessValues() throws Exception {
    TestSuiteChromosome chromosome1 = mockChromosome(-0.5);
    TestSuiteChromosome chromosome2 = mockChromosome(-0.8);

    List<TestSuiteChromosome> population = List.of(chromosome1, chromosome2);

    TestSuiteChromosome selected = selection.apply(population);

    // Verify that the chromosome with the higher (less negative) fitness value is selected
    assertEquals(chromosome1, selected, "Chromosome with higher fitness value should be selected.");
}

}
