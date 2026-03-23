package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.Random;

/**
 * A single-point crossover operator for test suite chromosomes.
 */
public class SinglePointCrossover implements Crossover<TestSuiteChromosome> {

    private final Random random;

    public SinglePointCrossover(Random random) {
        this.random = random;
    }

    @Override
    public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();

        // Select a random crossover point
        int crossoverPoint = random.nextInt(genes1.length);

        // Create offspring by combining genes
        boolean[] offspring1Genes = new boolean[genes1.length];
        boolean[] offspring2Genes = new boolean[genes2.length];
        for (int i = 0; i < genes1.length; i++) {
            if (i < crossoverPoint) {
                offspring1Genes[i] = genes1[i];
                offspring2Genes[i] = genes2[i];
            } else {
                offspring1Genes[i] = genes2[i];
                offspring2Genes[i] = genes1[i];
            }
        }

        // Create offspring chromosomes
        TestSuiteChromosome offspring1 = new TestSuiteChromosome(offspring1Genes, parent1.getMutation(), parent1.getCrossover());
        TestSuiteChromosome offspring2 = new TestSuiteChromosome(offspring2Genes, parent2.getMutation(), parent2.getCrossover());

        // Ensure validity
        if (!offspring1.isValid()) {
            makeValid(offspring1Genes);
            offspring1 = new TestSuiteChromosome(offspring1Genes, parent1.getMutation(), parent1.getCrossover());
        }
        if (!offspring2.isValid()) {
            makeValid(offspring2Genes);
            offspring2 = new TestSuiteChromosome(offspring2Genes, parent2.getMutation(), parent2.getCrossover());
        }

        return Pair.of(offspring1, offspring2);
    }

    /**
     * Makes the given gene array valid by flipping one random bit to true.
     *
     * @param genes the gene array to modify
     */
    private void makeValid(boolean[] genes) {
        int index = random.nextInt(genes.length);
        genes[index] = true;
    }

    @Override
    public String toString() {
        return "Single-Point Crossover";
    }
}
