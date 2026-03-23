package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BinaryTournamentSelectionTest {

    private Comparator<TestSuiteChromosome> comparator;
    private Random random;
    private BinaryTournamentSelection<TestSuiteChromosome> selection;

    @BeforeEach
    void setup() {
        // Mock comparator
        comparator = mock(Comparator.class);

        // Mock random
        random = mock(Random.class);

        // Create selection instance
        selection = new BinaryTournamentSelection<>(comparator, random);
    }

    @Test
    void testApplySelectsBetterChromosome() {
        // Create a mock population
        List<TestSuiteChromosome> population = List.of(
                mock(TestSuiteChromosome.class),
                mock(TestSuiteChromosome.class)
        );

        // Mock random to always select indices 0 and 1
        when(random.nextInt(population.size())).thenReturn(0, 1);

        // Mock comparator to favor the first chromosome
        when(comparator.compare(population.get(0), population.get(1))).thenReturn(1);

        // Apply selection
        TestSuiteChromosome result = selection.apply(population);

        // Assert the best chromosome is selected
        assertEquals(population.get(0), result);
    }

    @Test
void testApplyHandlesEqualFitnessChromosomes() {
    // Create mock chromosomes
    TestSuiteChromosome chromosome1 = mock(TestSuiteChromosome.class);
    TestSuiteChromosome chromosome2 = mock(TestSuiteChromosome.class);

    // Stub the comparator to return 0 (equal fitness for both)
    when(comparator.compare(chromosome1, chromosome2)).thenReturn(0);
    when(comparator.compare(chromosome2, chromosome1)).thenReturn(0);

    // Explicitly define tie-breaking in the comparator
    Comparator<TestSuiteChromosome> tieBreakingComparator = (c1, c2) -> {
        if (c1 == c2) return 0;
        return c1.hashCode() < c2.hashCode() ? -1 : 1;
    };

    BinaryTournamentSelection<TestSuiteChromosome> selection =
        new BinaryTournamentSelection<>(tieBreakingComparator, new Random());

    // Create a population
    List<TestSuiteChromosome> population = Arrays.asList(chromosome1, chromosome2);

    // Perform selection
    TestSuiteChromosome selected = selection.apply(population);

    // Assert that one of the chromosomes was selected
    assertTrue(selected == chromosome1 || selected == chromosome2, 
        "Selected chromosome should be one from the population.");
}




    @Test
    void testApplyThrowsExceptionForEmptyPopulation() {
        // Create an empty population
        List<TestSuiteChromosome> population = new ArrayList<>();

        // Assert exception is thrown
        assertThrows(NoSuchElementException.class, () -> selection.apply(population));
    }

    @Test
    void testApplyThrowsExceptionForNullPopulation() {
        // Assert exception is thrown
        assertThrows(NullPointerException.class, () -> selection.apply(null));
    }

    @Test
    void testApplyHandlesSingleElementPopulation() {
        // Create a single-element population
        List<TestSuiteChromosome> population = List.of(mock(TestSuiteChromosome.class));

        // Mock random to always select index 0
        when(random.nextInt(population.size())).thenReturn(0);

        // Apply selection
        TestSuiteChromosome result = selection.apply(population);

        // Assert the single chromosome is selected
        assertEquals(population.get(0), result);
    }

    @Test
    void testApplyHandlesRandomDistinctIndices() {
        // Create a mock population
        List<TestSuiteChromosome> population = List.of(
                mock(TestSuiteChromosome.class),
                mock(TestSuiteChromosome.class),
                mock(TestSuiteChromosome.class)
        );

        // Mock random to select distinct indices
        when(random.nextInt(population.size())).thenReturn(0, 2);

        // Mock comparator to favor the second selected chromosome
        when(comparator.compare(population.get(0), population.get(2))).thenReturn(-1);

        // Apply selection
        TestSuiteChromosome result = selection.apply(population);

        // Assert the better chromosome is selected
        assertEquals(population.get(2), result);
    }
}
