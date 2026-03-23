package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements single-point crossover for TestSuiteChromosome.
 */
public class TestSuiteCrossover implements Crossover<TestSuiteChromosome> {

    @Override
    public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
        List<Integer> testCases1 = parent1.getTestCases();
        List<Integer> testCases2 = parent2.getTestCases();

        // Check if either parent is empty
        if (testCases1.isEmpty() || testCases2.isEmpty()) {
            return Pair.of(parent1.copy(), parent2.copy());
        }

        // Perform single-point crossover
        Random random = new Random();
        int crossoverPoint = random.nextInt(Math.min(testCases1.size(), testCases2.size()));

        List<Integer> offspring1TestCases = new ArrayList<>(testCases1.subList(0, crossoverPoint));
        offspring1TestCases.addAll(testCases2.subList(crossoverPoint, testCases2.size()));

        List<Integer> offspring2TestCases = new ArrayList<>(testCases2.subList(0, crossoverPoint));
        offspring2TestCases.addAll(testCases1.subList(crossoverPoint, testCases1.size()));

        TestSuiteChromosome offspring1 = parent1.copy();
        offspring1.setTestCases(offspring1TestCases);

        TestSuiteChromosome offspring2 = parent2.copy();
        offspring2.setTestCases(offspring2TestCases);

        return Pair.of(offspring1, offspring2);
    }
}
