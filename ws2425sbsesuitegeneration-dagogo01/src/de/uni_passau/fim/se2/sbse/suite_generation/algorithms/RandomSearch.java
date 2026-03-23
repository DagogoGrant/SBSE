package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.ChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

import java.util.*;
import java.util.stream.Collectors;

public class RandomSearch<C extends Chromosome<C>> implements GeneticAlgorithm<C> {

    private final ChromosomeGenerator<C> generator;
    private final FitnessFunction<C> fitnessFunction;
    private final StoppingCondition stoppingCondition;
    private final int populationSize;
    private final Map<C, Map<Integer, Double>> fitnessMap = new HashMap<>();
    private final Set<Integer> targetBranchIds;

    public RandomSearch(ChromosomeGenerator<C> generator,
                        FitnessFunction<C> fitnessFunction,
                        StoppingCondition stoppingCondition,
                        int populationSize,
                        Set<Integer> targetBranchIds) {
        this.generator = Objects.requireNonNull(generator);
        this.fitnessFunction = Objects.requireNonNull(fitnessFunction);
        this.stoppingCondition = Objects.requireNonNull(stoppingCondition);
        this.populationSize = populationSize;
        this.targetBranchIds = Objects.requireNonNull(targetBranchIds);
    }
/**
 * Initializes a population of chromosomes and calculates their fitness.
 *
 * @return A list of initialized chromosomes.
 */
private List<C> initializePopulation() {
    List<C> population = new ArrayList<>();

    for (int i = 0; i < populationSize; i++) {
        // Generate an individual chromosome
        C individual = generator.get();

        // Optionally calculate and store fitness for branch coverage
        calculateFitnessIfApplicable(individual);

        // Add the individual to the population
        population.add(individual);
    }

    return population;
}

/**
 * Calculates and stores fitness for a chromosome if the fitness function supports branch coverage.
 *
 * @param individual The chromosome for which fitness is to be calculated.
 */
private void calculateFitnessIfApplicable(C individual) {
    if (fitnessFunction instanceof BranchCoverageFitnessFunction) {
        BranchCoverageFitnessFunction branchFitnessFunction = (BranchCoverageFitnessFunction) fitnessFunction;

        // Calculate branch fitness and store it in the fitness map
        Map<Integer, Double> branchFitness = branchFitnessFunction.calculateBranchFitness((TestChromosome) individual);
        fitnessMap.put(individual, branchFitness);
    }
}


private boolean dominates(C p, C q) {
    Map<Integer, Double> pFitness = fitnessMap.get(p);
    Map<Integer, Double> qFitness = fitnessMap.get(q);

    // Handle potential null values gracefully
    if (pFitness == null || qFitness == null) {
        return false;  // If either fitness value is missing, treat as non-dominating
    }

    boolean betterInOne = false;

    for (Integer branchId : targetBranchIds) {
        double pValue = pFitness.getOrDefault(branchId, Double.MAX_VALUE);
        double qValue = qFitness.getOrDefault(branchId, Double.MAX_VALUE);

        if (pValue > qValue) return false; // p is worse in this branch
        if (pValue < qValue) betterInOne = true; // p is better in at least one branch
    }
    return betterInOne;
}


    private List<C> getNonDominatedSolutions(List<C> population) {
        List<C> nonDominated = new ArrayList<>();
        for (C p : population) {
            boolean isDominated = false;
            for (C q : population) {
                if (dominates(q, p)) {
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated) nonDominated.add(p);
        }
        return nonDominated;
    }

    @Override
    public List<C> findSolution() {
        stoppingCondition.notifySearchStarted();
        List<C> solutions = new ArrayList<>();

        while (!stoppingCondition.searchMustStop()) {
            List<C> population = initializePopulation();
            List<C> nonDominated = getNonDominatedSolutions(population);
            solutions.addAll(nonDominated);
            stoppingCondition.notifyFitnessEvaluation();
        }

        return solutions;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}