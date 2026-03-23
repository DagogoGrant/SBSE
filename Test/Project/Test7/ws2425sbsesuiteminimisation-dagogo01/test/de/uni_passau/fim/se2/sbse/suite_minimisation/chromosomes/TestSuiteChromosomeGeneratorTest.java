package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteChromosomeGeneratorTest {

    @Test
    void testGenerateRandomChromosome() {
        int geneLength = 10;
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        ChromosomeGenerator<TestSuiteChromosome> generator = new TestSuiteChromosomeGenerator(geneLength, mutation, crossover);
        TestSuiteChromosome chromosome = generator.get();

        assertNotNull(chromosome);
        assertEquals(geneLength, chromosome.getGenes().length);
        assertTrue(hasAtLeastOneTrue(chromosome.getGenes()), "Generated chromosome must include at least one 'true'");
    }

    @Test
    void testGenerateChromosomeWithSmallGeneLength() {
        int geneLength = 1; // Smallest possible gene length
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        ChromosomeGenerator<TestSuiteChromosome> generator = new TestSuiteChromosomeGenerator(geneLength, mutation, crossover);
        TestSuiteChromosome chromosome = generator.get();

        assertNotNull(chromosome);
        assertEquals(geneLength, chromosome.getGenes().length);
        assertTrue(chromosome.getGenes()[0], "Generated chromosome must have 'true' for single-gene chromosomes");
    }

    @Test
    void testGenerateChromosomeWithLargeGeneLength() {
        int geneLength = 1000; // Large gene length
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        ChromosomeGenerator<TestSuiteChromosome> generator = new TestSuiteChromosomeGenerator(geneLength, mutation, crossover);
        TestSuiteChromosome chromosome = generator.get();

        assertNotNull(chromosome);
        assertEquals(geneLength, chromosome.getGenes().length);
        assertTrue(hasAtLeastOneTrue(chromosome.getGenes()), "Generated chromosome must include at least one 'true'");
    }

    private boolean hasAtLeastOneTrue(boolean[] genes) {
        for (boolean gene : genes) {
            if (gene) return true;
        }
        return false;
    }
}
