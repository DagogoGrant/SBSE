package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.stream.IntStream;

public class TestSuiteMutation implements Mutation<TestSuiteChromosome> {
    

    @Override
    public TestSuiteChromosome apply(final TestSuiteChromosome parent) {
        boolean[] genes = parent.getGenes().clone();
        int index = Randomness.random().nextInt(genes.length);

        // Flip the random gene
        genes[index] = !genes[index];

        // Ensure at least one gene is true
        if (IntStream.range(0, genes.length).noneMatch(i -> genes[i])) {
            genes[index] = true; // Revert the mutation
        }

        return new TestSuiteChromosome(genes, parent.getMutation(), parent.getCrossover());
    }

    @Override
    public String toString() {
        return "TestSuiteMutation";
    }
}
