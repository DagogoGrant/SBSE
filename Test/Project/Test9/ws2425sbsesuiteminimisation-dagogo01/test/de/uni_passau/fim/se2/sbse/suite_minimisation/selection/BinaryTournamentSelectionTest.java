package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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
    private List<TestSuiteChromosome> population;

    @BeforeEach
public void setUp() throws Exception {
    random = new Random();
    mutation = new TestSuiteMutation(0.1); // Specify mutation probability
    crossover = new TestSuiteCrossover();

    comparator = Comparator.comparingDouble(TestSuiteChromosome::getSizeFitness);

    selection = new BinaryTournamentSelection<>(comparator, random);

    population = new ArrayList<>();
    population.add(mockChromosome(0.1));
    population.add(mockChromosome(0.5));
    population.add(mockChromosome(0.8));
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
public void testApply_withPopulationOfTwo() throws Exception {
    Random fixedRandom = new Random(42); // Fixed seed for deterministic results
    BinaryTournamentSelection<TestSuiteChromosome> selection =
        new BinaryTournamentSelection<>(comparator, fixedRandom);

    List<TestSuiteChromosome> population = List.of(mockChromosome(0.1), mockChromosome(0.5));
    
    // Since the fixed seed ensures deterministic behavior, assert the expected winner
    assertEquals(population.get(0), selection.apply(population));
}

    @Test
    public void testApply_withPopulationOfOne() throws Exception {
        List<TestSuiteChromosome> singlePopulation = List.of(mockChromosome(0.3));
        assertEquals(singlePopulation.get(0), selection.apply(singlePopulation));
    }

    @Test
    public void testApply_withPopulation() throws Exception {
        Random fixedRandom = new Random(42);
        BinaryTournamentSelection<TestSuiteChromosome> selection = new BinaryTournamentSelection<>(comparator, fixedRandom);
        List<TestSuiteChromosome> largePopulation = generateLargePopulation(10);
        assertTrue(selection.apply(largePopulation) instanceof TestSuiteChromosome);
    }

    private List<TestSuiteChromosome> generateLargePopulation(int size) throws Exception {
        List<TestSuiteChromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(mockChromosome(random.nextDouble()));
        }
        return population;
    }
}
