package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.TestSuiteMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteCrossoverTest {

    private final TestSuiteMutation mutation = new TestSuiteMutation();
    private final TestSuiteCrossover crossover = new TestSuiteCrossover();

    @Test
    void testCrossoverCreatesOffspring() {
        boolean[] genes1 = {true, false, true};
        boolean[] genes2 = {false, true, false};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes1, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes2, mutation, crossover);

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertNotNull(offspring.getFst());
        assertNotNull(offspring.getSnd());

        // Ensure offspring have the same length as parents
        assertEquals(genes1.length, offspring.getFst().getGenes().length);
        assertEquals(genes2.length, offspring.getSnd().getGenes().length);
    }

    @Test
    void testCrossoverWithIdenticalParents() {
        boolean[] genes = {true, false, true};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes, mutation, crossover);

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertEquals(parent1, offspring.getFst());
        assertEquals(parent2, offspring.getSnd());
    }

    @Test
    void testCrossoverWithSingleGene() {
        boolean[] genes1 = {true};
        boolean[] genes2 = {false};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes1, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes2, mutation, crossover);

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertEquals(genes1.length, offspring.getFst().getGenes().length);
        assertEquals(genes2.length, offspring.getSnd().getGenes().length);
    }

    @Test
    void testCrossoverWithEmptyGenes() {
        boolean[] genes1 = {};
        boolean[] genes2 = {};

        TestSuiteChromosome parent1 = new TestSuiteChromosome(genes1, mutation, crossover);
        TestSuiteChromosome parent2 = new TestSuiteChromosome(genes2, mutation, crossover);

        Pair<TestSuiteChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertEquals(0, offspring.getFst().getGenes().length);
        assertEquals(0, offspring.getSnd().getGenes().length);
    }
}
