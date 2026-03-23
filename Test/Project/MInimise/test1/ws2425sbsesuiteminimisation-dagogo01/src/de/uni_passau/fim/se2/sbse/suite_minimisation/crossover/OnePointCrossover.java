package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

public class OnePointCrossover implements Crossover<TestSuiteChromosome> {

    @Override
    public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();
        int length = genes1.length;

        // Select a random crossover point
        int crossoverPoint = Randomness.random().nextInt(length);

        // Create offspring by combining genes
        boolean[] offspring1 = new boolean[length];
        boolean[] offspring2 = new boolean[length];
        for (int i = 0; i < length; i++) {
            if (i < crossoverPoint) {
                offspring1[i] = genes1[i];
                offspring2[i] = genes2[i];
            } else {
                offspring1[i] = genes2[i];
                offspring2[i] = genes1[i];
            }
        }

        return Pair.of(
            new TestSuiteChromosome(offspring1, parent1.getMutation(), this),
            new TestSuiteChromosome(offspring2, parent2.getMutation(), this)
        );
    }

    @Override
    public String toString() {
        return "OnePointCrossover";
    }
}
