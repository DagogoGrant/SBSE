package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

import java.util.Random;

public class TestSuiteMutation implements Mutation<TestSuiteChromosome> {

    private final Random random;
    private final double mutationProbability;

    /**
     * Constructor to initialize mutation with a specific probability.
     *
     * @param mutationProbability the probability of mutating a gene (0.0 to 1.0)
     */
    public TestSuiteMutation(double mutationProbability) {
        if (mutationProbability < 0.0 || mutationProbability > 1.0) {
            throw new IllegalArgumentException("Mutation probability must be between 0.0 and 1.0");
        }
        this.random = new Random();
        this.mutationProbability = mutationProbability;
    }

    @Override
    public TestSuiteChromosome apply(final TestSuiteChromosome chromosome) {
        if (chromosome == null || chromosome.getTestCases().length == 0) {
            throw new IllegalArgumentException("Chromosome or its test cases cannot be null or empty.");
        }
    
        // Create a copy of the parent chromosome
        TestSuiteChromosome mutatedChromosome = chromosome.copy();
    
        // Get the genes (test case selection array) and directly modify them
        boolean[] genes = mutatedChromosome.getTestCases();
    
        // Flip a random bit
        int indexToFlip = random.nextInt(genes.length);
        genes[indexToFlip] = !genes[indexToFlip]; // Toggle the boolean value
    
        // Return the mutated chromosome (genes already modified)
        return mutatedChromosome;
    }
    

}
