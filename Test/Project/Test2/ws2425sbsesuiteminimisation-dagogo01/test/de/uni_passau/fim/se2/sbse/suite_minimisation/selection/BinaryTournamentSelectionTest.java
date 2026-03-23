package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.SizeFitnessFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BinaryTournamentSelectionTest {

    @Test
    void testApplyWithValidPopulation() {
        // Mock coverage matrix
        boolean[][] coverageMatrix = {
                {true, false, true},
                {false, true, true},
                {true, true, false}
        };

        // Fitness functions
        CoverageFitnessFunction coverageFitness = new CoverageFitnessFunction(coverageMatrix);
        SizeFitnessFunction sizeFitness = new SizeFitnessFunction(3);

        // Mock population
        List<TestSuiteChromosome> population = new ArrayList<>();
        population.add(new TestSuiteChromosome(new boolean[]{true, false, true}));
        population.add(new TestSuiteChromosome(new boolean[]{false, true, false}));

        // Coverage comparator
        Comparator<TestSuiteChromosome> coverageComparator = Comparator.comparingDouble(coverageFitness::applyAsDouble);

        // Size comparator
        Comparator<TestSuiteChromosome> sizeComparator = Comparator.comparingDouble(sizeFitness::applyAsDouble);

        // Create selection operators
        BinaryTournamentSelection<TestSuiteChromosome> coverageSelection = new BinaryTournamentSelection<>(coverageComparator, new Random());
        BinaryTournamentSelection<TestSuiteChromosome> sizeSelection = new BinaryTournamentSelection<>(sizeComparator, new Random());

        // Apply selection with coverage fitness
        TestSuiteChromosome coverageSelected = coverageSelection.apply(population);
        assertTrue(population.contains(coverageSelected), "The selected chromosome (coverage) must be part of the population.");

        // Apply selection with size fitness
        TestSuiteChromosome sizeSelected = sizeSelection.apply(population);
        assertTrue(population.contains(sizeSelected), "The selected chromosome (size) must be part of the population.");
    }

    @Test
    void testApplyWithInvalidPopulationThrowsExceptions() {
        // Empty population
        List<TestSuiteChromosome> emptyPopulation = new ArrayList<>();

        // Custom comparator
        Comparator<TestSuiteChromosome> comparator = Comparator.comparingInt(c -> c.getGenes().length);

        // Create selection operator
        BinaryTournamentSelection<TestSuiteChromosome> selection = new BinaryTournamentSelection<>(comparator, new Random());

        // Expect NoSuchElementException for empty population
        assertThrows(NoSuchElementException.class, () -> selection.apply(emptyPopulation), "Applying selection to an empty population should throw a NoSuchElementException.");

        // Expect NullPointerException for null population
        assertThrows(NullPointerException.class, () -> selection.apply(null), "Applying selection to a null population should throw a NullPointerException.");
    }
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS) // Set timeout to 5 seconds
    void testApplyWithCoverageFitnessComparator() {
        // Test logic here
    }
}
