package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSuiteChromosomeTest {

    @Test
    void testConstructorValidInput() {
        boolean[] genes = {true, false, true};
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, mutation, crossover);

        assertNotNull(chromosome);
        assertArrayEquals(genes, chromosome.getGenes());
    }

    @Test
    void testConstructorInvalidInput() {
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        assertThrows(NullPointerException.class, () -> new TestSuiteChromosome(null, mutation, crossover));
    }

    @Test
    void testGetGenes() {
        boolean[] genes = {true, false, false, true};
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, mutation, crossover);

        assertArrayEquals(genes, chromosome.getGenes());
    }

    @Test
    void testCopy() {
        boolean[] genes = {true, false, true};
        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome = new TestSuiteChromosome(genes, mutation, crossover);
        TestSuiteChromosome copy = chromosome.copy();

        assertNotSame(chromosome, copy);
        assertArrayEquals(chromosome.getGenes(), copy.getGenes());
    }

    @Test
    void testEqualsAndHashCode() {
        boolean[] genes1 = {true, false, true};
        boolean[] genes2 = {true, false, true};
        boolean[] genes3 = {false, true, false};

        Mutation<TestSuiteChromosome> mutation = Mutation.identity();
        Crossover<TestSuiteChromosome> crossover = Crossover.identity();

        TestSuiteChromosome chromosome1 = new TestSuiteChromosome(genes1, mutation, crossover);
        TestSuiteChromosome chromosome2 = new TestSuiteChromosome(genes2, mutation, crossover);
        TestSuiteChromosome chromosome3 = new TestSuiteChromosome(genes3, mutation, crossover);

        assertEquals(chromosome1, chromosome2);
        assertNotEquals(chromosome1, chromosome3);
        assertEquals(chromosome1.hashCode(), chromosome2.hashCode());
    }
}
