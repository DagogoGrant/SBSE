package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Fitness function for branch coverage.
 */
public class BranchCoverageFitnessFunction implements FitnessFunction<TestChromosome> {

    private final Set<Integer> targetBranchIds;
    private final boolean minimize;

    /**
     * Constructs a fitness function for branch coverage.
     *
     * @param targetBranchIds The IDs of the branches to target.
     * @param minimize        Whether the fitness function minimizes the distance.
     */
    public BranchCoverageFitnessFunction(Set<Integer> targetBranchIds, boolean minimize) {
        this.targetBranchIds = targetBranchIds;
        this.minimize = minimize;
    }
    

    @Override
    public double applyAsDouble(TestChromosome testChromosome) {
        // Calculate fitness for all target branches
        double totalFitness = 0.0;

        for (int branchId : targetBranchIds) {
            Double distance = testChromosome.call().get(branchId);
            if (distance == null) {
                distance = 1.0; // Maximum distance for uncovered branch
            }

            // Normalize the distance and apply minimization/maximization
            double normalizedFitness = distance / (distance + 1);
            totalFitness += minimize ? normalizedFitness : 1.0 - normalizedFitness;
        }

        // Return the average fitness across all target branches
        return totalFitness / targetBranchIds.size();
    }

    @Override
    public boolean isMinimizing() {
        return minimize;
    }

    /**
     * Calculates the fitness for each branch individually.
     *
     * @param testChromosome The test chromosome to evaluate.
     * @return A map of branch IDs to their fitness values.
     */
    public Map<Integer, Double> calculateBranchFitness(TestChromosome testChromosome) {
        Map<Integer, Double> branchFitnessMap = new HashMap<>();
        Map<Integer, Double> distances = testChromosome.call();

        for (Integer branchId : targetBranchIds) {
            Double distance = distances.get(branchId);
            if (distance == null) {
                distance = 1.0; // Maximum distance for uncovered branch
            }

            // Normalize the distance
            double normalizedFitness = Math.min(distance / (distance + 1), 1.0);
            branchFitnessMap.put(branchId, normalizedFitness);
        }

        return branchFitnessMap;
    }
}
