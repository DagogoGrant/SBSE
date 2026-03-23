package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Randomness;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutation operator for TestSuiteChromosome.
 * Randomly flips a bit (adds or removes a test case).
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
     * Flips a bit by adding or removing a test case while ensuring a change occurs.
     *
     * @param parent the chromosome to mutate
     * @return a mutated copy of the chromosome
     */
    @Override
    public TestSuiteChromosome apply(TestSuiteChromosome parent) {
        Random random = Randomness.random();
        List<Integer> newTestCases = new ArrayList<>(parent.getTestCases());
    
        if (newTestCases.isEmpty()) {
            System.out.println("No mutation applied: Empty test cases.");
            return parent.copy(); // Return unchanged chromosome
        }
    
        boolean mutationApplied = false;
    
        for (int attempts = 0; attempts < 10 && !mutationApplied; attempts++) {
            int indexToMutate = random.nextInt(newTestCases.size());
    
            // Debugging logs
            System.out.println("Index to mutate: " + indexToMutate);
            System.out.println("Size of newTestCases: " + newTestCases.size());
    
            if (indexToMutate < 0 || indexToMutate >= newTestCases.size()) {
                System.err.println("Invalid mutation index: " + indexToMutate + " (newTestCases size: " + newTestCases.size() + ")");
                throw new IllegalArgumentException("Mutation index out of bounds: " + indexToMutate);
            }
    
            int originalValue = newTestCases.get(indexToMutate);
            int mutatedValue = (originalValue == 0) ? 1 : 0;
    
            if (mutatedValue != originalValue) {
                newTestCases.set(indexToMutate, mutatedValue);
                mutationApplied = true;
                System.out.println("Mutation applied at index " + indexToMutate);
            }
        }
    
        if (!mutationApplied) {
            System.out.println("No mutation applied after maximum attempts.");
            return parent.copy();
        }
    
        TestSuiteChromosome offspring = parent.copy();
        offspring.setTestCases(newTestCases);
    
        return offspring;
    }
    
    
}