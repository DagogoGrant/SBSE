package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;

import java.util.Map;
import java.util.Objects;

/**
 * A fitness function that evaluates how well a chromosome covers a specific branch in the CUT (Class Under Test).
 * Lower fitness values are better as this is a minimization problem.
 */
public class BranchCoverageFitnessFunction implements FitnessFunction<TestChromosome> {

    private final IBranch targetBranch;

    /**
     * Constructs a BranchCoverageFitnessFunction for a specific branch.
     *
     * @param targetBranch the branch to target
     */
    public BranchCoverageFitnessFunction(final IBranch targetBranch) {
        this.targetBranch = Objects.requireNonNull(targetBranch, "Target branch must not be null.");
    }

    /**
     * Computes the fitness value for the given chromosome.
     * The fitness value is based on the branch distance for the target branch.
     *
     * @param chromosome the test chromosome to evaluate
     * @return the fitness value (lower is better)
     * @throws NullPointerException if the given chromosome is null
     */
    @Override
    public double applyAsDouble(final TestChromosome testCase) {
        // Execute the test case to gather new branch distance information
        // An get the branch distances after test case execution
        Map<Integer, Double> distances = testCase.call();

        
        // Retrieve the distance for our target branch
        Double distance = distances.get(targetBranch.getId());

        // If the branch was not executed at all, return the highest possible distance (which should be worse than any real distance)
        if (distance == null) {
            // Assuming the maximum distance for an unexecuted branch is 1 (this can be adjusted based on your branch distance calculation method)
            return 1.0;
        }

        // The fitness is inversely proportional to the distance: 
        // a lower distance means a better fitness (closer to covering the branch)
        // Here, we normalize the distance to be within [0, 1], with 0 being the best (branch covered) and 1 the worst (branch not covered at all)
        return Math.min(distance / (distance + 1), 1.0); // Normalize to [0, 1]
    }


    /**
     * Indicates that this is a minimizing fitness function.
     *
     * @return true, as lower values are better
     */
    @Override
    public boolean isMinimizing() {
        return true;
    }

    @Override
    public String toString() {
        return "BranchCoverageFitnessFunction(targetBranch=" + targetBranch + ")";
    }
}
