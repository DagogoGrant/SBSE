package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestSuiteCrossover implements Crossover<TestSuiteChromosome> {

    private final Random random;

    // Default constructor for flexibility
    public TestSuiteCrossover() {
        this.random = new Random(); // Initialize with default Random
    }

    // Constructor allowing injection of Random object
    public TestSuiteCrossover(Random random) {
        this.random = random;
    }

    @Override
    public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        boolean[] genes1 = parent1.getTestCases();
        boolean[] genes2 = parent2.getTestCases();

        if (genes1.length != genes2.length) {
            throw new IllegalArgumentException("Parent chromosomes must have the same length.");
        }

        int length = genes1.length;
        int crossoverPoint = random.nextInt(length); // Random crossover point

        boolean[] offspring1Genes = new boolean[length];
        boolean[] offspring2Genes = new boolean[length];

        for (int i = 0; i < length; i++) {
            if (i < crossoverPoint) {
                offspring1Genes[i] = genes1[i];
                offspring2Genes[i] = genes2[i];
            } else {
                offspring1Genes[i] = genes2[i];
                offspring2Genes[i] = genes1[i];
            }
        }

        TestSuiteChromosome offspring1 = new TestSuiteChromosome(offspring1Genes, parent1.getMutation(), this);
        TestSuiteChromosome offspring2 = new TestSuiteChromosome(offspring2Genes, parent2.getMutation(), this);

        return Pair.of(offspring1, offspring2);
    }

    // Alternative method for list-based crossover
    public List<TestSuiteChromosome> crossover(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        Pair<TestSuiteChromosome> offspringPair = apply(parent1, parent2);
        List<TestSuiteChromosome> offspringList = new ArrayList<>();
        offspringList.add(offspringPair.getFst());
        offspringList.add(offspringPair.getSnd());
        return offspringList;
    }
}
