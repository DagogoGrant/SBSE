package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

public class TestSuiteCrossover implements Crossover<TestSuiteChromosome> {

    @Override
    public Pair<TestSuiteChromosome> apply(final TestSuiteChromosome parent1, final TestSuiteChromosome parent2) {
        boolean[] genes1 = parent1.getGenes().clone();
        boolean[] genes2 = parent2.getGenes().clone();

        // Handle empty genes
        if (genes1.length == 0 || genes2.length == 0) {
            return new Pair<>(
                    new TestSuiteChromosome(genes1, parent1.getMutation(), parent1.getCrossover()),
                    new TestSuiteChromosome(genes2, parent2.getMutation(), parent2.getCrossover())
            );
        }

        int crossoverPoint = Randomness.random().nextInt(genes1.length);

        // Perform single-point crossover
        for (int i = crossoverPoint; i < genes1.length; i++) {
            boolean temp = genes1[i];
            genes1[i] = genes2[i];
            genes2[i] = temp;
        }

        return new Pair<>(
                new TestSuiteChromosome(genes1, parent1.getMutation(), parent1.getCrossover()),
                new TestSuiteChromosome(genes2, parent2.getMutation(), parent2.getCrossover())
        );
    }

    @Override
    public String toString() {
        return "TestSuiteCrossover";
    }
}
