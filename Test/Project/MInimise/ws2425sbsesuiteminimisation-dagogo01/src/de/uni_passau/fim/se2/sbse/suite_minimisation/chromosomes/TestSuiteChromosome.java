package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;

import java.util.Arrays;

public class TestSuiteChromosome extends Chromosome<TestSuiteChromosome> {

    private final boolean[] testSuite;

    /**
     * Constructs a new TestSuiteChromosome.
     *
     * @param testSuite the test suite encoding (boolean array)
     * @param mutation  the mutation operator
     * @param crossover the crossover operator
     */
    public TestSuiteChromosome(boolean[] testSuite, Mutation<TestSuiteChromosome> mutation, Crossover<TestSuiteChromosome> crossover) {
        super(mutation, crossover);
        this.testSuite = Arrays.copyOf(testSuite, testSuite.length);
    }

    /**
     * Copy constructor.
     *
     * @param other the chromosome to copy
     */
    public TestSuiteChromosome(TestSuiteChromosome other) {
        super(other);
        this.testSuite = Arrays.copyOf(other.testSuite, other.testSuite.length);
    }

    @Override
    public TestSuiteChromosome copy() {
        return new TestSuiteChromosome(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TestSuiteChromosome)) return false;
        TestSuiteChromosome that = (TestSuiteChromosome) other;
        return Arrays.equals(this.testSuite, that.testSuite);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(testSuite);
    }

    /**
     * Returns the test suite encoding.
     *
     * @return the boolean array representing the test suite
     */
    public boolean[] getTestSuite() {
        return Arrays.copyOf(testSuite, testSuite.length);
    }

    /**
     * Returns the current instance of this chromosome.
     *
     * @return this chromosome instance
     */
    @Override
    public TestSuiteChromosome self() {
        return this;
    }
}
