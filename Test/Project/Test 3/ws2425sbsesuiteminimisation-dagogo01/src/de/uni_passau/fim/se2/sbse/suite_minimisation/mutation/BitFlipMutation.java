package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

import java.util.Random;

/**
 * A mutation operator that flips a random bit in the chromosome's genes.
 */
public class BitFlipMutation implements Mutation<TestSuiteChromosome> {

    private final Random random;

    public BitFlipMutation(Random random) {
        this.random = random;
    }

    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        boolean[] genes = parent.getGenes().clone();

        // Flip a random bit
        int index = random.nextInt(genes.length);
        genes[index] = !genes[index];

        // Ensure validity: at least one test case must be selected
        boolean valid = false;
        for (boolean gene : genes) {
            if (gene) {
                valid = true;
                break;
            }
        }

        // If invalid, flip another random bit
        if (!valid) {
            index = random.nextInt(genes.length);
            genes[index] = true;
        }

        return new TestSuiteChromosome(genes, parent.getMutation(), parent.getCrossover());
    }

    @Override
    public String toString() {
        return "Bit-Flip Mutation";
    }
}
