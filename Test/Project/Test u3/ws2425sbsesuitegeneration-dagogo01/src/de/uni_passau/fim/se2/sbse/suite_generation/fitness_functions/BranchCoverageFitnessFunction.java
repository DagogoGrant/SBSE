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
    public double applyAsDouble(final TestChromosome chromosome) {
        System.out.println("[DEBUG] Calculating fitness for target branch: " + targetBranch.getId());

        try {
            // Ensure the BranchTracer is initialized and cleared
            BranchTracer tracer = BranchTracer.getInstance();
            tracer.clear();

            // Execute the chromosome and collect branch distances
            Map<Integer, Double> branchDistances = chromosome.call();

            // Retrieve the branch distance for the target branch
            double branchDistance = branchDistances.getOrDefault(targetBranch.getId(), Double.MAX_VALUE);

            // Normalize the branch distance
            double normalizedDistance = branchDistance / (1.0 + branchDistance);

            // Return the fitness value
            return normalizedDistance;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] Fitness calculation failed: " + e.getMessage());
            return Double.MAX_VALUE; // High penalty on error
        }
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
