package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;


import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteChromosomeTest {

    @Test
    void testRandomChromosomeGeneration() {
        int totalTestCases = 10;
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(mutation, crossover, totalTestCases);

        assertFalse(chromosome.getTestCases().isEmpty(), "Chromosome should have at least one test case");
        assertTrue(chromosome.getTestCases().size() <= totalTestCases, "Chromosome test cases should not exceed totalTestCases");
    }

    @Test
    void testCopyConstructor() {
        int totalTestCases = 10;
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome original = new TestSuiteChromosome(mutation, crossover, totalTestCases);
        TestSuiteChromosome copy = original.copy();

        assertEquals(original.getTestCases(), copy.getTestCases(), "Copied chromosome should have the same test cases");
        assertNotSame(original, copy, "Copied chromosome should not be the same reference as the original");
    }
}
