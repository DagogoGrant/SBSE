package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;


import javax.sound.sampled.AudioFormat.Encoding;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.selection.RankSelection;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class MOSA implements GeneticAlgorithm<TestChromosome> {
    private final int populationSize;
    private final Random random;
    private final TestChromosomeGenerator generator;
    private final List<Branch> targetBranches;
    private final Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions;
    private final StoppingCondition stoppingCondition;
    private final List<TestChromosome> archive;

    public MOSA(
        int populationSize,
        Random random,
        TestChromosomeGenerator generator,
        List<Branch> targetBranches,
        StoppingCondition stoppingCondition
    ) {
        this.populationSize = requireNonNull(populationSize);
        this.random = requireNonNull(random);
        this.generator = requireNonNull(generator);
        this.targetBranches = requireNonNull(targetBranches);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.archive = new ArrayList<>();

        // Initialize fitness functions
        this.fitnessFunctions = targetBranches.stream()
            .collect(Collectors.toMap(
                branch -> branch,
                branch -> new BranchCoverageFitnessFunction(Set.of(branch.getId()), true)
            ));
    }

    @Override
    public List<TestChromosome> findSolution() {
        resetArchive();
        notifySearchStart();
    
        while (!shouldStopSearch()) {
            List<TestChromosome> population = generateInitialPopulation();
            Map<TestChromosome, Map<Branch, Double>> fitnessMap = computeFitness(population);
            storeInArchive(population, fitnessMap);
    
            List<TestChromosome> offspring = createOffspring(population, fitnessMap);
            List<TestChromosome> combinedPopulation = mergePopulations(population, offspring);
    
            Map<TestChromosome, Map<Branch, Double>> updatedFitnessMap = computeFitness(combinedPopulation);
            List<List<TestChromosome>> sortedFronts = sortNonDominated(combinedPopulation, updatedFitnessMap);
    
            for (List<TestChromosome> front : sortedFronts) {
                calculateDensity(front, updatedFitnessMap);
            }
    
            notifyEvaluation();
        }
    
        return retrieveFinalArchive();
    }
    
    private void resetArchive() {
        archive.clear();
    }
    
    private void notifySearchStart() {
        stoppingCondition.notifySearchStarted();
    }
    
    private boolean shouldStopSearch() {
        return stoppingCondition.searchMustStop();
    }
    
    private List<TestChromosome> generateInitialPopulation() {
        return initializePopulation(populationSize, generator);
    }
    
    private Map<TestChromosome, Map<Branch, Double>> computeFitness(List<TestChromosome> population) {
        return evaluateFitness(population, targetBranches, fitnessFunctions);
    }
    
    private void storeInArchive(List<TestChromosome> population, Map<TestChromosome, Map<Branch, Double>> fitnessMap) {
        updateArchive(population, fitnessMap, archive, targetBranches);
    }
    
    private List<TestChromosome> createOffspring(List<TestChromosome> population, Map<TestChromosome, Map<Branch, Double>> fitnessMap) {
        return generateOffspring(population, fitnessMap);
    }
    
    private List<TestChromosome> mergePopulations(List<TestChromosome> population, List<TestChromosome> offspring) {
        List<TestChromosome> combined = new ArrayList<>(population);
        combined.addAll(offspring);
        return combined;
    }
    
    private List<List<TestChromosome>> sortNonDominated(List<TestChromosome> population, Map<TestChromosome, Map<Branch, Double>> fitnessMap) {
        return nonDominatedSorting(population, fitnessMap, targetBranches);
    }
    
    private void calculateDensity(List<TestChromosome> front, Map<TestChromosome, Map<Branch, Double>> fitnessMap) {
        calculateSubvectorDensity(front, fitnessMap);
    }
    
    private void notifyEvaluation() {
        stoppingCondition.notifyFitnessEvaluation();
    }
    
    private List<TestChromosome> retrieveFinalArchive() {
        return new ArrayList<>(archive);
    }
    

    private static List<TestChromosome> initializePopulation(int populationSize, TestChromosomeGenerator generator) {
        List<TestChromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generator.get());
        }
        return population;
    }

    private static void updateArchive(
        List<TestChromosome> population,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap,
        List<TestChromosome> archive,
        List<Branch> targetBranches
    ) {
        // Add logic to update the archive with non-dominated solutions
        archive.addAll(population);
    }

    private static List<List<TestChromosome>> nonDominatedSorting(
        List<TestChromosome> population,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
    ) {
        return new ArrayList<>();
    }

    private static boolean dominates(
        TestChromosome p1,
        TestChromosome p2,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
    ) {
        return true;
    }

    private static Map<TestChromosome, Map<Branch, Double>> evaluateFitness(
        List<TestChromosome> population,
        List<Branch> targetBranches,
        Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions
    ) {
        Map<TestChromosome, Map<Branch, Double>> fitnessMap = new HashMap<>();

        for (TestChromosome testCase : population) {
            Map<Branch, Double> branchFitness = new HashMap<>();
            for (Branch branch : targetBranches) {
                double fitness = fitnessFunctions.get(branch).applyAsDouble(testCase);
                branchFitness.put(branch, fitness);
            }
            fitnessMap.put(testCase, branchFitness);
        }

        return fitnessMap;
    }

    private void calculateSubvectorDensity(
        List<TestChromosome> front,
        Map<TestChromosome, Map<Branch, Double>> fitnessMap
    ) {
        int objectiveCount = targetBranches.size();
        double[][] subvectorDistances = new double[front.size()][objectiveCount];

        for (int obj = 0; obj < targetBranches.size(); obj++) {
            Branch branch = targetBranches.get(obj);
            front.sort(Comparator.comparingDouble(tc -> fitnessMap.get(tc).get(branch)));

            for (int i = 0; i < front.size(); i++) {
                double lower = (i == 0) ? Double.POSITIVE_INFINITY :
                        fitnessMap.get(front.get(i)).get(branch) - fitnessMap.get(front.get(i - 1)).get(branch);
                double upper = (i == front.size() - 1) ? Double.POSITIVE_INFINITY :
                        fitnessMap.get(front.get(i + 1)).get(branch) - fitnessMap.get(front.get(i)).get(branch);

                subvectorDistances[i][obj] = lower + upper;
            }
        }

        for (int i = 0; i < front.size(); i++) {
            double density = Arrays.stream(subvectorDistances[i]).sum();
            front.get(i).setDensity(density);
        }
    }

    private List<TestChromosome> generateOffspring(
        List<TestChromosome> population, Map<TestChromosome,
        Map<Branch, Double>> fitnessMap
    ) {
        List<TestChromosome> offspringPopulation = new ArrayList<>();

        Comparator<TestChromosome> comparator = (p1, p2) ->
                dominates(p1, p2, fitnessMap, targetBranches) ? 1 : -1;

        RankSelection<TestChromosome> selection = new RankSelection<>(
            comparator, population.size(), 1.9, random
        );

        TestChromosome offspring1;
        TestChromosome offspring2;
        while (offspringPopulation.size() < population.size()) {
            TestChromosome parent1 = selection.apply(population);
            TestChromosome parent2 = selection.apply(population);
            if (random.nextDouble() < 0.8) {
                Pair<TestChromosome> pair = parent1.crossover(parent2);
                offspring1 = pair.getFst();
                offspring2 = pair.getSnd();
            } else {
                offspring1 = parent1;
                offspring2 = parent2;
            }
            offspring1 = offspring1.mutate();
            offspring2 = offspring2.mutate();

            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }
        return offspringPopulation;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
