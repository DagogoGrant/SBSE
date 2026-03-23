package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutation operator for TestSuiteChromosome.
 * Randomly adds or removes a test case.
 */
public class TestSuiteMutation implements Mutation<TestSuiteChromosome> {

    private final double mutationProbability; // Probability of performing a mutation

    /**
     * Constructs a TestSuiteMutation operator with a given probability.
     *
     * @param mutationProbability the probability of applying mutation (0.0 - 1.0)
     */
    public TestSuiteMutation(double mutationProbability) {
        if (mutationProbability < 0.0 || mutationProbability > 1.0) {
            throw new IllegalArgumentException("Mutation probability must be between 0.0 and 1.0");
        }
        this.mutationProbability = mutationProbability;
    }

    /**
     * Applies mutation to the given chromosome.
     * Either adds or removes a test case based on a random decision.
     *
     * @param parent the chromosome to mutate
     * @return a mutated copy of the chromosome
     */
    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        Random random = Randomness.random();
        List<Integer> newTestCases = new ArrayList<>(parent.getTestCases());
        int totalTestCases = parent.getTotalTestCases();
        List<Integer> previousTestCases = new ArrayList<>(newTestCases);
    
        boolean mutationApplied = false;
    
        while (!mutationApplied) {
            if (random.nextBoolean() && !newTestCases.isEmpty()) {
                // Remove a random test case
                int randomIndex = random.nextInt(newTestCases.size());
                newTestCases.remove(randomIndex);
                mutationApplied = true;
            } else {
                // Add a random test case ensuring it's new
                int newTestCase;
                do {
                    newTestCase = random.nextInt(totalTestCases);
                } while (newTestCases.contains(newTestCase));
    
                newTestCases.add(newTestCase);
                mutationApplied = true;
            }
    
            // Ensure the size is different from the parent chromosome
            if (newTestCases.size() != previousTestCases.size()) {
                mutationApplied = true;
            }
        }
    
        // Create offspring
        TestSuiteChromosome offspring = parent.copy();
        offspring.setTestCases(newTestCases);
        return offspring;
    }
    
    



    @Override
    public String toString() {
        return "TestSuiteMutation{" +
                "mutationProbability=" + mutationProbability +
                '}';
    }
}
