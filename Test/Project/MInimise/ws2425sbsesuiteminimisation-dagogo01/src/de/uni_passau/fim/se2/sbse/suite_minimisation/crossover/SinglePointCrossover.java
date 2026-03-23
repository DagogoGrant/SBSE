package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.Random;

public class SinglePointCrossover implements Crossover<TestSuiteChromosome> {

    @Override
    public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        boolean[] parent1Suite = parent1.getTestSuite();
        boolean[] parent2Suite = parent2.getTestSuite();

        if (parent1Suite.length != parent2Suite.length) {
            throw new IllegalArgumentException("Parent chromosomes must have the same length.");
        }

        boolean[] offspring1Suite = new boolean[parent1Suite.length];
        boolean[] offspring2Suite = new boolean[parent1Suite.length];
        Random random = Randomness.random();

        // Choose a random crossover point
        int crossoverPoint = random.nextInt(parent1Suite.length);

        // Perform crossover
        for (int i = 0; i < parent1Suite.length; i++) {
            if (i < crossoverPoint) {
                offspring1Suite[i] = parent1Suite[i];
                offspring2Suite[i] = parent2Suite[i];
            } else {
                offspring1Suite[i] = parent2Suite[i];
                offspring2Suite[i] = parent1Suite[i];
            }
        }

        // Create offspring chromosomes
        TestSuiteChromosome offspring1 = new TestSuiteChromosome(offspring1Suite, parent1.getMutation(), parent1.getCrossover());
        TestSuiteChromosome offspring2 = new TestSuiteChromosome(offspring2Suite, parent2.getMutation(), parent2.getCrossover());

        return Pair.of(offspring1, offspring2);
    }
}
