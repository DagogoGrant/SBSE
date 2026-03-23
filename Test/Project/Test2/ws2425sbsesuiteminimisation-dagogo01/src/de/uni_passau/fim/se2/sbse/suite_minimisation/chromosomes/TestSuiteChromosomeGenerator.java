package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import java.util.Random;

/**
 * A generator for random TestSuiteChromosomes.
 */
public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {

    private final int size; // Number of test cases
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;
    private final Random random = new Random(); // Random number generator

    /**
     * Constructs a generator with the given chromosome size, mutation, and crossover operators.
     *
     * @param size      number of test cases
     * @param mutation  mutation operator
     * @param crossover crossover operator
     */
    public TestSuiteChromosomeGenerator(int size, Mutation<TestSuiteChromosome> mutation, Crossover<TestSuiteChromosome> crossover) {
        this.size = size;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    /**
     * Creates and returns a random chromosome with valid genes.
     *
     * @return a valid random TestSuiteChromosome
     */
    @Override
    public TestSuiteChromosome get() {
        boolean[] genes = new boolean[size];
        // Ensure at least one gene is true
        boolean hasTrue = false;
        for (int i = 0; i < size; i++) {
            genes[i] = random.nextBoolean();
            if (genes[i]) hasTrue = true;
        }
        // If all genes are false, set one random gene to true
        if (!hasTrue) {
            genes[random.nextInt(size)] = true;
        }
        return new TestSuiteChromosome(genes, mutation, crossover);
    }
}
