package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuiteChromosomeGeneratorTest {

    private TestSuiteChromosomeGenerator generator;
    private Mutation<TestSuiteChromosome> mutation;
    private Crossover<TestSuiteChromosome> crossover;
    private Random random;

    @BeforeEach
    void setUp() {
        mutation = chromosome -> chromosome; // Identity mutation for simplicity
        crossover = (parent1, parent2) -> new Pair<>(parent1, parent2); // Identity crossover
        random = new Random(42); // Seeded random for reproducibility
        generator = new TestSuiteChromosomeGenerator(5, mutation, crossover, random);
    }

    @Test
    void testGenerateValidChromosome() {
        TestSuiteChromosome chromosome = generator.get();
        assertNotNull(chromosome, "Generated chromosome should not be null.");
        assertEquals(5, chromosome.getTestCases().length, "Chromosome should have 5 test cases.");
    }

    @Test
    void testAtLeastOneTestCaseIncluded() {
        TestSuiteChromosome chromosome = generator.get();
        boolean atLeastOneIncluded = false;
        for (boolean testCase : chromosome.getTestCases()) {
            if (testCase) {
                atLeastOneIncluded = true;
                break;
            }
        }
        assertTrue(atLeastOneIncluded, "Chromosome should include at least one test case.");
    }

    @Test
    void testRandomnessInGeneration() {
        TestSuiteChromosome first = generator.get();
        TestSuiteChromosome second = generator.get();
        assertNotEquals(first, second, "Two generated chromosomes should not be identical.");
    }
}
