package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.*;



/**
 * Crossover operator for TestSuiteChromosome.
 * Performs one-point crossover to combine two parent chromosomes.
 */
public class TestSuiteCrossover implements Crossover<TestSuiteChromosome> {

   @Override
public Pair<TestSuiteChromosome> apply(TestSuiteChromosome parent1, TestSuiteChromosome parent2) {
    Random random = Randomness.random();

    // Get test case lists from parents
    List<Integer> testCases1 = new ArrayList<>(parent1.getTestCases());
    List<Integer> testCases2 = new ArrayList<>(parent2.getTestCases());

    // Select a random crossover point
    int crossoverPoint1 = random.nextInt(testCases1.size());
    int crossoverPoint2 = random.nextInt(testCases2.size());

    // Create offspring by combining parts from both parents
    Set<Integer> offspring1Set = new LinkedHashSet<>(testCases1.subList(0, crossoverPoint1));
    offspring1Set.addAll(testCases2.subList(crossoverPoint2, testCases2.size()));

    Set<Integer> offspring2Set = new LinkedHashSet<>(testCases2.subList(0, crossoverPoint2));
    offspring2Set.addAll(testCases1.subList(crossoverPoint1, testCases1.size()));

    // Create offspring chromosomes
    TestSuiteChromosome offspring1 = parent1.copy();
    TestSuiteChromosome offspring2 = parent2.copy();

    // Replace test cases using setTestCases()
    offspring1.setTestCases(new ArrayList<>(offspring1Set));
    offspring2.setTestCases(new ArrayList<>(offspring2Set));

    return Pair.of(offspring1, offspring2);
}



    @Override
    public String toString() {
        return "TestSuiteCrossover{One-point crossover}";
    }
}
