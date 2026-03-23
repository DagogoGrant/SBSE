package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.DummyChromosomeFixture;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BinaryTournamentSelectionTest {

    @Test
void testSelectionReturnsFitterChromosome() {
    // Comparator comparing chromosomes by suite size
    Comparator<DummyChromosomeFixture> comparator = Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize);
    BinaryTournamentSelection<DummyChromosomeFixture> selection =
            new BinaryTournamentSelection<>(comparator, new Random(42)); // Fixed seed for reproducibility

    // Define chromosomes
    DummyChromosomeFixture fitter = new DummyChromosomeFixture(3, 4);  // Fitter chromosome
    DummyChromosomeFixture lessFit = new DummyChromosomeFixture(1, 2);

    // Create population
    List<DummyChromosomeFixture> population = Arrays.asList(fitter, lessFit);

    // Apply selection
    DummyChromosomeFixture selected = selection.apply(population);

    // Assert fitter chromosome is selected
    assertEquals(fitter, selected, "The fitter chromosome should always be selected.");
}

    @Test
    void testSelectionUsesComparator() {
        DummyChromosomeFixture chromosome1 = new DummyChromosomeFixture(1, 0);
        DummyChromosomeFixture chromosome2 = new DummyChromosomeFixture(3, 0);

        Comparator<DummyChromosomeFixture> comparator =
                Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize);
        BinaryTournamentSelection<DummyChromosomeFixture> selection =
                new BinaryTournamentSelection<>(comparator, new Random(42));

        DummyChromosomeFixture selected = selection.apply(List.of(chromosome1, chromosome2));
        assertEquals(chromosome1, selected, "Chromosome1 should be selected as it has the smallest suite size.");
    }

    @Test
    void testSelectionThrowsExceptionForEmptyPopulation() {
        BinaryTournamentSelection<DummyChromosomeFixture> selection =
                new BinaryTournamentSelection<>(Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize), new Random());
    
        assertThrows(NoSuchElementException.class,
            () -> selection.apply(Collections.emptyList()),
            "An exception should be thrown for an empty population.");
    }
    

    @Test
    void testSelectionHandlesSingleChromosomePopulation() {
        BinaryTournamentSelection<DummyChromosomeFixture> selection =
                new BinaryTournamentSelection<>(Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize), new Random());
    
        List<DummyChromosomeFixture> population = List.of(new DummyChromosomeFixture(1, 2));
    
     assertThrows(NoSuchElementException.class,
                () -> selection.apply(population),
                "An exception should be thrown for a single element population.");
    }
    


    @Test
    void testSelectionHandlesTieCases() {
        DummyChromosomeFixture chromosome1 = new DummyChromosomeFixture(2, 0);
        DummyChromosomeFixture chromosome2 = new DummyChromosomeFixture(2, 0); // Same fitness

        Comparator<DummyChromosomeFixture> comparator =
                Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize);
        BinaryTournamentSelection<DummyChromosomeFixture> selection =
                new BinaryTournamentSelection<>(comparator, new Random(42));

        DummyChromosomeFixture selected = selection.apply(List.of(chromosome1, chromosome2));
        assertTrue(selected.equals(chromosome1) || selected.equals(chromosome2),
                "In a tie case, one of the chromosomes should be selected.");
    }
    @Test
void testSelectionThrowsExceptionForSinglePopulation() {
    BinaryTournamentSelection<DummyChromosomeFixture> selection =
            new BinaryTournamentSelection<>(Comparator.comparingInt(DummyChromosomeFixture::getSuiteSize), new Random());

    List<DummyChromosomeFixture> population = List.of(new DummyChromosomeFixture(1, 2));

    assertThrows(NoSuchElementException.class,
        () -> selection.apply(population),
        "An exception should be thrown for a single element population.");
}



}
