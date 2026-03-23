package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;

import java.util.Map;

/**
 * A fitness function that evaluates how well a chromosome covers branches in the CUT (Class Under Test).
 * Lower fitness values are better as this is a minimization problem.
 */
public class BranchCoverageFitnessFunction implements FitnessFunction<TestChromosome> {

    private final Class<?> cut;

    /**
     * Constructs a BranchCoverageFitnessFunction for a specific Class Under Test (CUT).
     *
     * @param cut the Class Under Test
     */
    public BranchCoverageFitnessFunction(final Class<?> cut) {
        if (cut == null) {
            throw new IllegalArgumentException("Class Under Test (CUT) must not be null.");
        }
        this.cut = cut;
    }

    /**
     * Computes the fitness value for the given chromosome.
     * The fitness value is calculated based on approach level and normalized branch distances for all uncovered branches.
     *
     * @param chromosome the test chromosome to evaluate
     * @return the fitness value (lower is better)
     * @throws NullPointerException if the given chromosome is null
     */
    @Override
    public double applyAsDouble(final TestChromosome chromosome) {
        System.out.println("[DEBUG] Calculating fitness for a chromosome");

        try {
            // Ensure the BranchTracer is initialized and cleared
            BranchTracer tracer = BranchTracer.getInstance();
            tracer.clear();

            // Execute the chromosome and collect branch distances
            Map<Integer, Double> branchDistances = chromosome.call();

            // Validate branchDistances
            if (branchDistances == null || branchDistances.isEmpty()) {
                System.err.println("[WARNING] No branch distances found. Returning high fitness.");
                return Double.MAX_VALUE; // High penalty for no coverage
            }

            // Initialize total fitness and tracking variables
            double totalFitness = 0.0;
            int totalBranches = branchDistances.size();
            int coveredBranches = 0;

            // Process each branch
            for (Map.Entry<Integer, Double> entry : branchDistances.entrySet()) {
                int branchID = entry.getKey();
                double rawDistance = entry.getValue();

                // Compute normalized branch distance
                double normalizedDistance = rawDistance / (1 + rawDistance);

                // Retrieve approach level from the tracer (or assume default if not tracked)
                int approachLevel = tracer.getApproachLevel(branchID); // Implement this in BranchTracer if needed

                // Calculate fitness for this branch
                double branchFitness = approachLevel + normalizedDistance;

                if (rawDistance == 0.0) {
                    // Fully covered branch
                    coveredBranches++;
                    System.out.printf("[DEBUG] Branch %d is fully covered.\n", branchID);
                } else {
                    // Add branch fitness to total
                    totalFitness += branchFitness;
                    System.out.printf("[DEBUG] Branch %d is not covered. Fitness: %.2f\n", branchID, branchFitness);
                }
            }

            // Normalize the fitness value
            if (totalBranches > 0) {
                totalFitness /= totalBranches;
            }

            // Debugging summary
            if (coveredBranches == totalBranches) {
                System.out.println("[INFO] All branches are fully covered!");
            } else {
                System.out.printf("[DEBUG] Covered branches: %d / %d\n", coveredBranches, totalBranches);
            }

            System.out.printf("[DEBUG] Normalized fitness value: %.2f\n", totalFitness);
            return totalFitness;

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
}
