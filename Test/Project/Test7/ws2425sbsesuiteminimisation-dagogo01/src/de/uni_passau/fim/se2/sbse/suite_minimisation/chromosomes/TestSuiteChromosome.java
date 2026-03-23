package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.Objects;
import java.util.stream.IntStream;

public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private final boolean[] genes;

    /**
     * Constructs a TestSuiteChromosome.
     *
     * @param genes     the gene representation of the chromosome.
     * @param mutation  the mutation operator (use identity if null).
     * @param crossover the crossover operator (use identity if null).
     */
    public TestSuiteChromosome(boolean[] genes, Mutation<TestSuiteChromosome> mutation, Crossover<TestSuiteChromosome> crossover) {
        super(
            mutation != null ? mutation : Mutation.identity(),
            crossover != null ? crossover : Crossover.identity()
        );
        this.genes = Objects.requireNonNull(genes).clone();
    }

    @Override
    public TestSuiteChromosome self() {
        return this;
    }

    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(genes, getMutation(), getCrossover());
    }

    public boolean[] getGenes() {
        return genes.clone();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome that = (TestSuiteChromosome) other;
        return java.util.Arrays.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(genes);
    }
}
