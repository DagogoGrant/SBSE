package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteCrossoverTest {

    @Test
    void testSinglePointCrossover() {
        Random random = new Random(42);

        // Provide identity mutation and crossover for testing
        Mutation<TestSuiteChromosome> mutation = chromosome -> chromosome.copy();
        Crossover<TestSuiteChromosome> crossover = (parent1, parent2) -> Pair.of(parent1.copy(), parent2.copy());

        boolean[] genes1 = {true, false, true, false};
        boolean[] genes2 = {false, true, false, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes1, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes2, mutation, crossover);

        TestSuiteCrossover testCrossover = new TestSuiteCrossover(random);
        Pair<TestSuiteChromosome> offspring = testCrossover.apply(parent1, parent2);

        assertNotNull(offspring.getFst(), "First offspring should not be null.");
        assertNotNull(offspring.getSnd(), "Second offspring should not be null.");
        assertEquals(genes1.length, offspring.getFst().getTestCases().length, "Offspring should have the same length as parents.");
        assertEquals(genes2.length, offspring.getSnd().getTestCases().length, "Offspring should have the same length as parents.");
    }
}
