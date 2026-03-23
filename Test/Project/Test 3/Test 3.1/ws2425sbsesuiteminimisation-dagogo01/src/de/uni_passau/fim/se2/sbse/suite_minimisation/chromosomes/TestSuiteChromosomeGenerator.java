package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;

/**
 * A generator for creating random TestSuiteChromosome instances.
 */
public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int totalTestCases; // Total test cases available
    private final Mutation<TestSuiteChromosome> mutation; // Mutation operator
    private final Crossover<TestSuiteChromosome> crossover; // Crossover operator

    /**
     * Constructs a generator for TestSuiteChromosomes.
     *
     * @param totalTestCases the total number of test cases in the suite
     * @param mutation       the mutation operator
     * @param crossover      the crossover operator
     */
    public TestSuiteChromosomeGenerator(int totalTestCases, 
                                        Mutation<TestSuiteChromosome> mutation, 
                                        Crossover<TestSuiteChromosome> crossover) {
        this.totalTestCases = totalTestCases;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    /**
     * Generates a random TestSuiteChromosome.
     *
     * @return a valid, random TestSuiteChromosome
     */
    @Override
    public TestSuiteChromosome get() {
        return new TestSuiteChromosome(mutation, crossover, totalTestCases);
    }
}
