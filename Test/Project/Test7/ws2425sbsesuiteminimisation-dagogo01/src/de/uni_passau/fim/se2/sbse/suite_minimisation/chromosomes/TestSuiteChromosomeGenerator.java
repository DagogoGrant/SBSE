package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;


import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;


public class TestSuiteChromosomeGenerator implements ChromosomeGenerator<TestSuiteChromosome> {
    private final int geneLength;
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;

    public TestSuiteChromosomeGenerator(int geneLength, Mutation<TestSuiteChromosome> mutation, Crossover<TestSuiteChromosome> crossover) {
        this.geneLength = geneLength;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    @Override
    public TestSuiteChromosome get() {
        boolean[] genes = new boolean[geneLength];
        boolean hasAtLeastOneTrue = false;

        for (int i = 0; i < geneLength; i++) {
            genes[i] = Randomness.random().nextBoolean();
            if (genes[i]) {
                hasAtLeastOneTrue = true;
            }
        }

        // Ensure at least one gene is true
        if (!hasAtLeastOneTrue) {
            genes[Randomness.random().nextInt(geneLength)] = true;
        }

        return new TestSuiteChromosome(genes, mutation, crossover);
    }
}


