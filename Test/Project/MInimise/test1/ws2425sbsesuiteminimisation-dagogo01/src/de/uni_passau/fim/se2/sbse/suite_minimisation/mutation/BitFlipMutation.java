package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

public class BitFlipMutation implements Mutation<TestSuiteChromosome> {

    private final double mutationRate; // Probability of flipping each gene

    public BitFlipMutation(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        boolean[] genes = parent.getGenes().clone();
        for (int i = 0; i < genes.length; i++) {
            if (Randomness.random().nextDouble() < mutationRate) {
                genes[i] = !genes[i]; // Flip the bit
            }
        }
        return new TestSuiteChromosome(genes, this, parent.getCrossover());
    }

    @Override
    public String toString() {
        return "BitFlipMutation with rate " + mutationRate;
    }
}
