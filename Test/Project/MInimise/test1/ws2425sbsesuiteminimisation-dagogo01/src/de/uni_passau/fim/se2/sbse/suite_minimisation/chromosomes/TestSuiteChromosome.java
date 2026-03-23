package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.Arrays;
import java.util.Objects;

public final class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private final boolean[] genes; // Binary vector representing selected test cases

    // Existing constructor that accepts mutation and crossover
    public TestSuiteChromosome(boolean[] genes, Mutation<TestSuiteChromosome> mutation, 
                               Crossover<TestSuiteChromosome> crossover) {
        super(mutation, crossover);
        this.genes = Objects.requireNonNull(genes).clone();
    }

    // Overloaded constructor without mutation and crossover (simpler for testing)
    public TestSuiteChromosome(boolean[] genes) {
        super(); // Calls the parent constructor with identity mutation and crossover
        this.genes = Objects.requireNonNull(genes).clone();
    }

    public boolean[] getGenes() {
        return genes.clone();
    }

    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(this.genes, this.getMutation(), this.getCrossover());
    }

    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestSuiteChromosome that = (TestSuiteChromosome) o;
        return Arrays.equals(this.getGenes(), that.getGenes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getGenes());
    }
}
