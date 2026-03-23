package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for TestSuiteChromosome.
 */
public class TestSuiteChromosomeTest {

    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;

    @BeforeEach
    void setup() {
        mutation = mock(Mutation.class);
        crossover = mock(Crossover.class);
    }

    @Test
    void testAddTestCase() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.addTestCase(1);

        assertTrue(chromosome.getTestCases().contains(1), "Test case should be added.");
    }

    @Test
    void testAddDuplicateTestCase() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.addTestCase(1);
        chromosome.addTestCase(1); // Add duplicate

        assertEquals(1, chromosome.getTestCases().stream().filter(tc -> tc == 1).count(),
                "Duplicate test cases should not be added.");
    }

    @Test
    void testRemoveTestCase() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.addTestCase(1);
        chromosome.removeTestCase(1);

        assertFalse(chromosome.getTestCases().contains(1), "Test case should be removed.");
    }

    @Test
    void testSetTestCases() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.setTestCases(Arrays.asList(1, 2, 3));

        assertEquals(Arrays.asList(1, 2, 3), chromosome.getTestCases(), "Test cases should be set correctly.");
    }

    @Test
    void testCopy() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.setTestCases(Arrays.asList(1, 2, 3));

        TestSuiteChromosome copy = chromosome.copy();

        assertNotSame(chromosome, copy, "Copy should create a new instance.");
        assertEquals(chromosome.getTestCases(), copy.getTestCases(), "Copy should have the same test cases.");
    }

    @Test
    void testEqualsAndHashCode() {
        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(mutation, crossover, 10);
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(mutation, crossover, 10);

        chromosome1.setTestCases(Arrays.asList(1, 2, 3));
        chromosome2.setTestCases(Arrays.asList(1, 2, 3));

        assertEquals(chromosome1, chromosome2, "Chromosomes with the same test cases should be equal.");
        assertEquals(chromosome1.hashCode(), chromosome2.hashCode(), "Hash codes should match for equal chromosomes.");
    }

    @Test
    void testEmptyChromosome() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 0);

        assertTrue(chromosome.getTestCases().isEmpty(), "Chromosome should start empty.");
    }

    @Test
    void testToString() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);
        chromosome.setTestCases(Arrays.asList(1, 2, 3));

        String expected = "TestSuiteChromosome{testCases=[1, 2, 3]}";
        assertEquals(expected, chromosome.toString(), "toString should produce the correct format.");
    }

    @Test
    void testTotalTestCases() {
        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, 10);

        assertEquals(10, chromosome.getTotalTestCases(), "Total test cases should be correctly reported.");
    }
}
