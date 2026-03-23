package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

import java.util.Random;

public class TestSuiteMutation implements Mutation<TestSuiteChromosome> {

    private final double mutationProbability;
    private final Random random;

    // Constructor with mutation probability and random generator
    public TestSuiteMutation(double mutationProbability, Random random) {
        if (mutationProbability < 0 || mutationProbability > 1) {
            throw new IllegalArgumentException("Mutation probability must be between 0 and 1.");
        }
        this.mutationProbability = mutationProbability;
        this.random = random;
    }

    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        boolean[] genes = parent.getTestCases();
        boolean[] mutatedGenes = genes.clone();

        // Apply mutation with probability
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationProbability) {
                mutatedGenes[i] = !mutatedGenes[i]; // Flip the bit
            }
        }

        // Ensure at least one test case is selected
        if (!isValid(mutatedGenes)) {
            mutatedGenes[random.nextInt(genes.length)] = true; // Force at least one bit to be true
        }

        return new TestSuiteChromosome(mutatedGenes, this, parent.getCrossover());
    }

    private boolean isValid(boolean[] genes) {
        for (boolean gene : genes) {
            if (gene) return true; // Valid if at least one bit is true
        }
        return false;
    }
}
