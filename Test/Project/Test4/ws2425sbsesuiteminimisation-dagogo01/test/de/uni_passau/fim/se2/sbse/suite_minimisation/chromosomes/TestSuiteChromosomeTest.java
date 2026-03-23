package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuiteChromosomeTest {

    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;
    private TestSuiteChromosome chromosome;

    @BeforeEach
    void setUp() {
        mutation = chromosome -> chromosome; // Identity mutation for simplicity
        crossover = (parent1, parent2) -> new Pair<>(parent1, parent2); // Identity crossover
        boolean[] testCases = {true, false, true, false, true};
        chromosome = new TestSuiteChromosome(testCases, mutation, crossover);
    }

    @Test
    void testCopy() {
        TestSuiteChromosome copy = chromosome.copy();
        assertNotSame(chromosome, copy, "Copied chromosome should not be the same instance.");
        assertArrayEquals(chromosome.getTestCases(), copy.getTestCases(), "Copied chromosome should have identical test cases.");
    }

    @Test
    void testSelfMethod() {
        assertSame(chromosome, chromosome.self(), "self() method should return the same instance.");
    }

    @Test
    void testEqualsAndHashCode() {
        boolean[] sameTestCases = {true, false, true, false, true};
        TestSuiteChromosome sameChromosome = new TestSuiteChromosome(sameTestCases, mutation, crossover);

        boolean[] differentTestCases = {false, false, true, false, true};
        TestSuiteChromosome differentChromosome = new TestSuiteChromosome(differentTestCases, mutation, crossover);

        assertEquals(chromosome, sameChromosome, "Chromosomes with the same test cases should be equal.");
        assertNotEquals(chromosome, differentChromosome, "Chromosomes with different test cases should not be equal.");
        assertEquals(chromosome.hashCode(), sameChromosome.hashCode(), "Hash codes for identical chromosomes should match.");
    }

    @Test
    void testToString() {
        String expected = "TestSuiteChromosome{testCases=" + Arrays.toString(chromosome.getTestCases()) + "}";
        assertEquals(expected, chromosome.toString(), "toString() method should return the expected string.");
    }
}
