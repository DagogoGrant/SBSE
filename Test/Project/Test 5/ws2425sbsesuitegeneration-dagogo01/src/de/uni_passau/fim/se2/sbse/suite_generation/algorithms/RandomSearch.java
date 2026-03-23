package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RandomSearch implements GeneticAlgorithm<TestChromosome> {

    private final Random random = Randomness.random(); // Source of randomness
    private final StoppingCondition stoppingCondition; // Stopping condition for termination
    private final TestChromosomeGenerator generator; // Generates random chromosomes
    private final Set<IBranch> branchesToCover; // Branches to be covered
    private final int populationSize; // Number of chromosomes per generation
    private final Map<IBranch, FitnessFunction<TestChromosome>> fitnessFunctions; // Fitness functions for branches
    private final List<TestChromosome> archive; // Stores the best solutions

    /**
     * Constructs a RandomSearch instance.
     *
     * @param stoppingCondition the stopping condition
     * @param generator         the chromosome generator
     * @param branchesToCover   the branches to optimize for
     * @param populationSize    the size of the population
     */
    public RandomSearch(
            StoppingCondition stoppingCondition,
            TestChromosomeGenerator generator,
            Set<IBranch> branchesToCover,
            int populationSize
    ) {
        this.stoppingCondition = Objects.requireNonNull(stoppingCondition);
        this.generator = Objects.requireNonNull(generator);
        this.branchesToCover = Objects.requireNonNull(branchesToCover);
        this.populationSize = populationSize;
        this.archive = new ArrayList<>();

        // Initialize fitness functions for each branch
        this.fitnessFunctions = branchesToCover.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        branch -> new BranchCoverageFitnessFunction(branch)
                ));
    }

    /**
     * Executes the random search and returns the archive.
     *
     * @return the archive containing the best solutions
     */
    public List<TestChromosome> findSolution() {
        archive.clear();
        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            // Step 1: Initialize population
            List<TestChromosome> population = initializePopulation(populationSize);

            // Step 2: Evaluate fitness
            Map<TestChromosome, Map<IBranch, Double>> fitnessMap = evaluateFitness(population);

            // Step 3: Update archive
            updateArchive(population, fitnessMap);

            stoppingCondition.notifyFitnessEvaluation();
        }

        return archive;
    }

    /**
     * Initializes a random population of the specified size.
     */
    private List<TestChromosome> initializePopulation(int size) {
        List<TestChromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    /**
     * Evaluates the fitness of each chromosome for all branches.
     */
    private Map<TestChromosome, Map<IBranch, Double>> evaluateFitness(List<TestChromosome> population) {
        if (population.isEmpty() || branchesToCover.isEmpty()) {
            throw new RuntimeException("Population or branches to cover cannot be empty");
        }
    
        Map<TestChromosome, Map<IBranch, Double>> fitnessMap = new HashMap<>();
    
        for (TestChromosome chromosome : population) {
            Map<IBranch, Double> branchFitness = new HashMap<>();
            for (IBranch branch : branchesToCover) {
                FitnessFunction<TestChromosome> fitnessFunction = fitnessFunctions.get(branch);
                if (fitnessFunction == null) {
                    throw new RuntimeException("No fitness function defined for branch: " + branch);
                }
                branchFitness.put(branch, fitnessFunction.applyAsDouble(chromosome));
            }
            fitnessMap.put(chromosome, branchFitness);
        }
    
        return fitnessMap;
    }
    
    /**
     * Updates the archive with non-dominated solutions.
     */
    private void updateArchive(List<TestChromosome> population, Map<TestChromosome, Map<IBranch, Double>> fitnessMap) {
        for (TestChromosome chromosome : population) {
            boolean isNonDominated = true;
            for (TestChromosome archived : archive) {
                if (dominates(archived, chromosome, fitnessMap)) {
                    isNonDominated = false;
                    break;
                }
            }
            if (isNonDominated) {
                archive.add(chromosome);
            }
        }
    }

    /**
     * Checks dominance between two chromosomes.
     */
    private boolean dominates(TestChromosome a, TestChromosome b, Map<TestChromosome, Map<IBranch, Double>> fitnessMap) {
        Map<IBranch, Double> aFitness = fitnessMap.get(a);
        Map<IBranch, Double> bFitness = fitnessMap.get(b);
        boolean atLeastOneBetter = false;

        for (IBranch branch : branchesToCover) {
            double aValue = aFitness.get(branch);
            double bValue = bFitness.get(branch);
            if (aValue > bValue) {
                return false;
            }
            if (aValue < bValue) {
                atLeastOneBetter = true;
            }
        }
        return atLeastOneBetter;
    }
    @Override
public StoppingCondition getStoppingCondition() {
    return stoppingCondition;
}

}
