package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.Arrays;

/**
 * Concrete implementation of a Chromosome for test suite minimisation.
 */
public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    // Binary vector to represent the test suite
    private final boolean[] genes;

    /**
     * Constructs a TestSuiteChromosome with the given genes, mutation, and crossover operators.
     *
     * @param genes     binary vector representing the test suite
     * @param mutation  mutation operator
     * @param crossover crossover operator
     */
    public TestSuiteChromosome(boolean[] genes, Mutation<TestSuiteChromosome> mutation, Crossover<TestSuiteChromosome> crossover) {
        super(mutation, crossover);
        this.genes = genes.clone(); // Clone to avoid reference issues
    }

    /**
     * Constructs a TestSuiteChromosome with identity mutation and crossover for testing purposes.
     *
     * @param genes binary vector representing the test suite
     */
    public TestSuiteChromosome(boolean[] genes) {
        super();
        this.genes = genes.clone();
    }

    /**
     * Ensures the chromosome is valid (at least one test case is selected).
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        for (boolean gene : genes) {
            if (gene) return true;
        }
        return false; // All genes are false, invalid chromosome
    }

    /**
     * Returns the binary vector representing this chromosome.
     *
     * @return the genes
     */
    public boolean[] getGenes() {
        return genes.clone();
    }

    /**
     * Creates a deep copy of this chromosome.
     *
     * @return a new TestSuiteChromosome with the same genes
     */
    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(genes, getMutation(), getCrossover());
    }
    /**
     * Provides the current instance of this class.
     *
     * @return this instance of TestSuiteChromosome
     */
    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    /**
     * Checks equality based on the genes.
     *
     * @param other the object to compare with
     * @return true if the chromosomes have the same genes, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome that = (TestSuiteChromosome) other;
        return Arrays.equals(this.genes, that.genes);
    }

    /**
     * Computes the hash code based on the genes.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }
}
