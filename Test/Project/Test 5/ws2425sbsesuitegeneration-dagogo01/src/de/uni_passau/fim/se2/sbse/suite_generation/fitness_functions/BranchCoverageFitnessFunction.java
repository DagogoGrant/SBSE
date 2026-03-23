package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;

import java.util.Map;

/**
 * Fitness function tailored to evaluate test chromosomes based on branch coverage and branch distances.
 * This function aims to maximize branch coverage while minimizing branch distances and test suite size.
 */
public class BranchCoverageFitnessFunction implements FitnessFunction<TestChromosome> {

    private static final double INVALID_PENALTY = -1000.0; // Penalty for invalid chromosomes
    private static final double UNCOVERED_BRANCH_PENALTY = -1.0; // Penalty for uncovered branches
    private static final double SIZE_PENALTY_FACTOR = 0.1; // Penalty factor for larger test suites
      private final IBranch branch; // Target branch for this fitness function
     /**
     * Constructor accepting a target branch identifier.
     *
     * @param targetBranch The branch identifier this fitness function evaluates.
     */
    public BranchCoverageFitnessFunction(IBranch branch) {
        this.branch = branch;
    }

    /**
     * Computes the fitness value of the given test chromosome.
     * The fitness value is based on:
     * - Higher branch coverage contributes positively.
     * - Lower branch distances contribute positively.
     * - Larger test suite sizes are penalized.
     * - Invalid solutions or execution failures incur heavy penalties.
     *
     * @param chromosome the test chromosome to evaluate
     * @return the fitness value (higher is better for this maximizing function)
     * @throws NullPointerException if the chromosome is null
     */
    @Override
    public double applyAsDouble(final TestChromosome chromosome) {
        if (chromosome == null) {
            throw new NullPointerException("Chromosome cannot be null");
        }

        // Use BranchTracer to gather branch coverage and distances
        BranchTracer branchTracer = BranchTracer.getInstance();
        branchTracer.clear();

        try {
            // Execute the chromosome
            chromosome.call();
        } catch (Exception e) {
            // Penalize execution failures
            System.err.println("Execution failed for chromosome: " + e.getMessage());
            return INVALID_PENALTY;
        }

        // Gather branch distance data
        Map<Integer, Double> distances = branchTracer.getDistances();

        if (distances.isEmpty()) {
            // Penalize chromosomes that don't cover any branches
            return INVALID_PENALTY;
        }

        // Calculate fitness based on branch distances
        double fitness = calculateBranchCoverageFitness(distances);

        // Apply additional penalties (e.g., test suite size)
        fitness -= chromosome.getStatements().size() * SIZE_PENALTY_FACTOR;

        return fitness;
    }

    /**
     * Calculates the fitness contribution based on branch coverage and distances.
     *
     * @param distances A map of branch IDs to their respective distances.
     * @return The calculated fitness value.
     */
    private double calculateBranchCoverageFitness(Map<Integer, Double> distances) {
        double fitness = 0.0;
        for (Map.Entry<Integer, Double> entry : distances.entrySet()) {
            double distance = entry.getValue();
            if (distance < 0) {
                // Penalize invalid branch distances
                fitness += UNCOVERED_BRANCH_PENALTY;
            } else {
                // Higher coverage and lower distance -> Higher fitness
                fitness += 1.0 / (1.0 + distance);
            }
        }
        return fitness;
    }

    /**
     * Indicates that this is a maximizing fitness function.
     *
     * @return false (indicating this is a maximizing fitness function)
     */
    @Override
    public boolean isMinimizing() {
        return false; // This is a maximizing function
    }

    /**
     * Provides a string representation of the fitness function.
     *
     * @return a string describing this fitness function
     */
    @Override
    public String toString() {
        return "BranchCoverageFitnessFunction targeting branch: " + branch.getId();
    }
}
