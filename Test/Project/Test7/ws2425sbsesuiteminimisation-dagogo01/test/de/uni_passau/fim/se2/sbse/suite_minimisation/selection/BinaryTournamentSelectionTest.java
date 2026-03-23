package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.TestSuiteCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BinaryTournamentSelectionTest {

    private BinaryTournamentSelection<TestSuiteChromosome> selection;
    private List<TestSuiteChromosome> population;
    private TestSuiteMutation mutation;
    private TestSuiteCrossover crossover;

    @BeforeEach
    void setUp() {
        mutation = new TestSuiteMutation();
        crossover = new TestSuiteCrossover();

        Comparator<TestSuiteChromosome> comparator = Comparator.comparingInt(c -> {
            int fitness = 0;
            for (boolean gene : c.getGenes()) {
                if (gene) fitness++;
            }
            return fitness;
        });

        selection = new BinaryTournamentSelection<>(comparator, new Random());

        population = new ArrayList<>();
        population.add(new TestSuiteChromosome(new boolean[]{true, false, true}, mutation, crossover)); // Fitness = 2
        population.add(new TestSuiteChromosome(new boolean[]{false, false, false}, mutation, crossover)); // Fitness = 0
        population.add(new TestSuiteChromosome(new boolean[]{true, true, true}, mutation, crossover)); // Fitness = 3
    }

    @Test
    void testBasicSelection() {
        TestSuiteChromosome selected = selection.apply(population);
        assertNotNull(selected, "The selection should return a chromosome.");
        assertTrue(population.contains(selected), "The selected chromosome must belong to the population.");
    }

    @Test
    void testSingleIndividualPopulation() {
        List<TestSuiteChromosome> singlePopulation = List.of(new TestSuiteChromosome(new boolean[]{true, true}, mutation, crossover));
        TestSuiteChromosome selected = selection.apply(singlePopulation);
        assertNotNull(selected, "The selection should return a chromosome.");
        assertEquals(singlePopulation.get(0), selected, "The single chromosome should always be selected.");
    }

    @Test
    void testEmptyPopulation() {
        List<TestSuiteChromosome> emptyPopulation = new ArrayList<>();
        assertThrows(NoSuchElementException.class, () -> selection.apply(emptyPopulation), "Should throw exception for empty population.");
    }

    @Test
    void testNullPopulation() {
        assertThrows(NullPointerException.class, () -> selection.apply(null), "Should throw exception for null population.");
    }

    @Test
    void testIdenticalIndividuals() {
        List<TestSuiteChromosome> identicalPopulation = new ArrayList<>();
        identicalPopulation.add(new TestSuiteChromosome(new boolean[]{true, false}, mutation, crossover));
        identicalPopulation.add(new TestSuiteChromosome(new boolean[]{true, false}, mutation, crossover));

        TestSuiteChromosome selected = selection.apply(identicalPopulation);
        assertNotNull(selected, "The selection should return a chromosome.");
        assertTrue(identicalPopulation.contains(selected), "The selected chromosome must belong to the population.");
    }
}
