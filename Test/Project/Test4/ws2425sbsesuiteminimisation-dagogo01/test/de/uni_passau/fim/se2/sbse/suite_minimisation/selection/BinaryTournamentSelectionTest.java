package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for BinaryTournamentSelection.
 */
public class BinaryTournamentSelectionTest {

    private BinaryTournamentSelection<TestSuiteChromosome> selection;
    private List<TestSuiteChromosome> population;
    private Random mockRandom;

    @BeforeEach
    void setUp() {
        // Create a mock Random object
        mockRandom = mock(Random.class);

        // Comparator for Pareto dominance: size fitness (lower is better)
        Comparator<TestSuiteChromosome> comparator = Comparator.comparingDouble(TestSuiteChromosome::getSizeFitness);

        // Instantiate the selection operator
        selection = new BinaryTournamentSelection<>(comparator, mockRandom);

        // Create mock chromosomes for testing
        TestSuiteChromosome chromosome1 = mockChromosome(0.2); // Better
        TestSuiteChromosome chromosome2 = mockChromosome(0.5); // Worse
        TestSuiteChromosome chromosome3 = mockChromosome(0.7);

        // Population setup
        population = Arrays.asList(chromosome1, chromosome2, chromosome3);
    }

    @Test
    void testBinaryTournamentSelectsBetterChromosome() {
        // Mock Random behavior: Select index 0 and 1
        when(mockRandom.nextInt(3)).thenReturn(0, 1);
    
        // Apply binary tournament selection
        TestSuiteChromosome winner = selection.apply(population);
    
        // Verify that the better chromosome is selected (chromosome1 with fitness 0.2)
        assertEquals(0.2, winner.getSizeFitness(), "The better chromosome should be selected.");
    }
    

    @Test
    void testThrowsExceptionWhenPopulationIsNull() {
        // Verify that exception is thrown for null population
        assertThrows(NullPointerException.class, () -> selection.apply(null));
    }

    @Test
    void testThrowsExceptionWhenPopulationIsEmpty() {
        List<TestSuiteChromosome> emptyPopulation = List.of();

        // Verify that exception is thrown for empty population
        assertThrows(NoSuchElementException.class, () -> selection.apply(emptyPopulation));
    }

    /**
     * Utility method to create a mock TestSuiteChromosome with a given size fitness.
     */
    private TestSuiteChromosome mockChromosome(double sizeFitness) {
        TestSuiteChromosome chromosome = mock(TestSuiteChromosome.class);
        when(chromosome.getSizeFitness()).thenReturn(sizeFitness);
        return chromosome;
    }
    
}
