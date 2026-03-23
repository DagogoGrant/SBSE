package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Implements the Random Search algorithm for generating test chromosomes.
 * This algorithm generates random chromosomes, evaluates them, and keeps track of the best solutions.
 */
public class RandomSearch implements GeneticAlgorithm<TestChromosome> {

    private final Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions;
    private final StoppingCondition stoppingCondition;
    private final TestChromosomeGenerator generator;
    private final List<Branch> targetBranches;
    private final int populationSize;
    private final List<TestChromosome> archive; // Archive to maintain the best solutions

    /**
     * Constructs a RandomSearch instance.
     *
     * @param stoppingCondition the stopping condition for the search
     * @param generator         the generator for creating random test chromosomes
     * @param targetBranches    the branches to target for coverage
     * @param populationSize    the size of the population to generate per iteration
     */
    public RandomSearch(
        StoppingCondition stoppingCondition,
        TestChromosomeGenerator generator,
        List<Branch> targetBranches,
        int populationSize
    ) {
        this.generator = requireNonNull(generator, "Generator must not be null.");
        this.stoppingCondition = requireNonNull(stoppingCondition, "Stopping condition must not be null.");
        this.targetBranches = requireNonNull(targetBranches, "Target branches must not be null.");
        if (populationSize <= 0) {
            throw new IllegalArgumentException("Population size must be greater than 0.");
        }
        this.populationSize = populationSize;
        this.archive = new ArrayList<>(); // Initialize archive

        // Initialize fitness functions for each target branch
        this.fitnessFunctions = targetBranches.stream()
            .collect(Collectors.toMap(branch -> branch, BranchCoverageFitnessFunction::new));
    }

    /**
     * Executes the Random Search algorithm to find solutions.
     *
     * @return a list of the best test chromosomes found
     */
    @Override
    public List<TestChromosome> findSolution() {
        archive.clear();
        stoppingCondition.notifySearchStarted();
    
        int iterationCount = 0; // Debug: Track iterations
        int maxIterations = 100; // Debug limit
    
        while (!stoppingCondition.searchMustStop() && iterationCount < maxIterations) {
            logDebug("Iteration: " + iterationCount);
            logDebug("Stopping condition met: " + stoppingCondition.searchMustStop());
            iterationCount++;
    
            // Generate population
            List<TestChromosome> population = Utils.initializePopulation(populationSize, generator);
            logDebug("Generated population of size: " + population.size());
    
            // Combine population with archive
            List<TestChromosome> combinedTestCases = new ArrayList<>(population);
            combinedTestCases.addAll(archive);
    
            // Log the population and archive before update
            logDebug("Population before update: " + population);
            logDebug("Archive before update: " + archive);
    
            // Evaluate fitness
            logDebug("Starting fitness evaluation...");
            Map<TestChromosome, Map<Branch, Double>> fitnessMap = Utils.evaluateFitness(
                combinedTestCases,
                targetBranches,
                fitnessFunctions
            );
            logDebug("Fitness evaluation completed.");
    
            // Update archive
            logDebug("Updating archive...");
            Utils.updateArchive(population, fitnessMap, archive, targetBranches);
            logDebug("Archive updated. Current archive size: " + archive.size());
    
            // Notify stopping condition
            stoppingCondition.notifyFitnessEvaluation();
        }
    
        if (iterationCount >= maxIterations) {
            logDebug("Terminating due to max iterations.");
        }
    
        return archive;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
    /**
 * Logs debug information to the console.
 *
 * @param message the message to log
 */
private static void logDebug(String message) {
    System.out.println("[DEBUG] " + message);
}

}


