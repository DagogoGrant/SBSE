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
     
         // Ensure at least one test case in the mutated chromosome
         if (newTestCases.isEmpty()) {
             newTestCases.add(random.nextInt(totalTestCases));
         }
     
         // Perform mutation (add or remove)
         if (random.nextBoolean() && !newTestCases.isEmpty()) {
             // Remove a random test case if there's more than one
             if (newTestCases.size() > 1) {
                 int randomIndex = random.nextInt(newTestCases.size());
                 newTestCases.remove(randomIndex);
             }
         } else {
             // Add a new test case ensuring it's not already present
             int newTestCase;
             do {
                 newTestCase = random.nextInt(totalTestCases);
             } while (newTestCases.contains(newTestCase));
     
             newTestCases.add(newTestCase);
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
