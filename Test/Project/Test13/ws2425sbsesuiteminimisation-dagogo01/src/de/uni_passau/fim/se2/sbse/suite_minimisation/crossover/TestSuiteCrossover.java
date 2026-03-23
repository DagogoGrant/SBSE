package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.Random;

public class TestSuiteCrossover implements Crossover<TestSuiteChromosome> {

    private final Random random;

    public TestSuiteCrossover() {
        this.random = new Random();
    }

    @Override
    public Pair<TestSuiteChromosome> apply(final TestSuiteChromosome parent1, final TestSuiteChromosome parent2) {
        // Randomly choose crossover method
        int method = random.nextInt(3); // 0: Single-point, 1: Multi-point, 2: Uniform
        switch (method) {
            case 0:
                return singlePointCrossover(parent1, parent2);
            case 1:
                return multiPointCrossover(parent1, parent2);
            default:
                return uniformCrossover(parent1, parent2);
        }
    }

    private Pair<TestSuiteChromosome> singlePointCrossover(final TestSuiteChromosome parent1, final TestSuiteChromosome parent2) {
        TestSuiteChromosome offspring1 = parent1.copy();
        TestSuiteChromosome offspring2 = parent2.copy();

        boolean[] genes1 = offspring1.getTestCases();
        boolean[] genes2 = offspring2.getTestCases();

        int crossoverPoint = random.nextInt(genes1.length); // Random crossover point
        for (int i = crossoverPoint; i < genes1.length; i++) {
            // Swap genes between the two offspring
            boolean temp = genes1[i];
            genes1[i] = genes2[i];
            genes2[i] = temp;
        }

        return Pair.of(offspring1, offspring2);
    }

    private Pair<TestSuiteChromosome> multiPointCrossover(final TestSuiteChromosome parent1, final TestSuiteChromosome parent2) {
        TestSuiteChromosome offspring1 = parent1.copy();
        TestSuiteChromosome offspring2 = parent2.copy();

        boolean[] genes1 = offspring1.getTestCases();
        boolean[] genes2 = offspring2.getTestCases();

        int point1 = random.nextInt(genes1.length);
        int point2 = random.nextInt(genes1.length);

        // Ensure point1 < point2
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        for (int i = point1; i < point2; i++) {
            boolean temp = genes1[i];
            genes1[i] = genes2[i];
            genes2[i] = temp;
        }

        return Pair.of(offspring1, offspring2);
    }

    private Pair<TestSuiteChromosome> uniformCrossover(final TestSuiteChromosome parent1, final TestSuiteChromosome parent2) {
        TestSuiteChromosome offspring1 = parent1.copy();
        TestSuiteChromosome offspring2 = parent2.copy();

        boolean[] genes1 = offspring1.getTestCases();
        boolean[] genes2 = offspring2.getTestCases();

        for (int i = 0; i < genes1.length; i++) {
            if (random.nextBoolean()) { // 50% probability
                boolean temp = genes1[i];
                genes1[i] = genes2[i];
                genes2[i] = temp;
            }
        }

        return Pair.of(offspring1, offspring2);
    }
}