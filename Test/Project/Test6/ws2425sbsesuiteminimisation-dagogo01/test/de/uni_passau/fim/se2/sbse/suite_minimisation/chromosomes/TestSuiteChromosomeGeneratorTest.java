package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for TestSuiteChromosomeGenerator.
 */
public class TestSuiteChromosomeGeneratorTest {

    @Test
void testGenerateRandomChromosome() {
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(10, mock(Mutation.class), mock(Crossover.class));
    TestSuiteChromosome chromosome = generator.get();

    assertNotNull(chromosome, "Generated chromosome should not be null.");
    assertTrue(chromosome.getTestCases().size() <= 10, "Chromosome size should not exceed total test cases.");
}
    @Test
void testGenerateWithZeroTestCases() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        new TestSuiteChromosomeGenerator(0, mock(Mutation.class), mock(Crossover.class));
    });
    assertEquals("Total test cases must be greater than 0.", exception.getMessage());
}


@Test
void testMultipleGenerations() {
    TestSuiteChromosomeGenerator generator = new TestSuiteChromosomeGenerator(
        5, // total test cases
        mock(Mutation.class),
        mock(Crossover.class)
    );

    TestSuiteChromosome chromosome1 = generator.get();
    TestSuiteChromosome chromosome2 = generator.get();

    // Ensure chromosomes are different by comparing test cases
    assertNotEquals(chromosome1.getTestCases(), chromosome2.getTestCases(),
        "Consecutive generations should produce different chromosomes.");

    // Log the test case details for debugging
    System.out.println("Chromosome 1: " + chromosome1.getTestCases());
    System.out.println("Chromosome 2: " + chromosome2.getTestCases());
}


}
