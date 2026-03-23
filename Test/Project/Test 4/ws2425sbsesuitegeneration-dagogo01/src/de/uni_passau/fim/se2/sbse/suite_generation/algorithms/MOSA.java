package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.selection.RankSelection;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

public class MOSA implements GeneticAlgorithm<TestCase> {
    private final int populationSize;
    private final Random random;
    private final TestCaseGenerator generator;
    private final List<Branch> targetBranches;
    private final Map<Branch, FitnessFunction<TestCase>> fitnessFunctions;
    private final StoppingCondition stoppingCondition;
    private final List<TestCase> archive; // Archive to maintain the best solutions
    private final RankSelection<TestCase> selection;

    public MOSA(
        int populationSize,
        Random random,
        TestCaseGenerator generator,
        List<Branch> targetBranches,
        StoppingCondition stoppingCondition,
        RankSelection<TestCase> selection
        ) {
        this.populationSize = requireNonNull(populationSize);
        this.random = requireNonNull(random);
        this.generator = requireNonNull(generator);
        this.targetBranches = requireNonNull(targetBranches);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.selection = requireNonNull(selection);
        this.archive = new ArrayList<>(); // Initialize archive

        // Initialize fitness functions for each target branch
        this.fitnessFunctions = targetBranches.stream()
            .collect(Collectors.toMap(branch -> branch, BranchCoverageFitnessFunction::new));
    }

    /**
     * {@inheritDoc}
     */
    public List<TestCase> findSolution() {
        
        List<TestCase> population = initializePopulation();
        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            // 1. Evaluate fitness for all target branches
            Map<TestCase, Map<Branch, Double>> fitnessMap = evaluateFitness(population);

            // 2. Update archive with the best solutions from the current generation
            updateArchive(population, fitnessMap);

            // 3. Sort by Pareto dominance
            List<List<TestCase>> fronts = nonDominatedSorting(population, fitnessMap);

            // 4. Estimate density using subvector dominance
            for (List<TestCase> front : fronts) {
                calculateSubvectorDensity(front, fitnessMap);
            }

            // 5. Build the next generation
            population = selectNextGeneration(fronts);
            // Notify the stoppingCodition
            stoppingCondition.notifyFitnessEvaluation();
        }

        return new ArrayList<>(archive);
    }

    private List<TestCase> initializePopulation() {
        List<TestCase> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generator.get());
        }
        return population;
    }

    private Map<TestCase, Map<Branch, Double>> evaluateFitness(List<TestCase> population) {
        Map<TestCase, Map<Branch, Double>> fitnessMap = new HashMap<>();

        for (TestCase testCase : population) {
            Map<Branch, Double> branchFitness = new HashMap<>();
            for (Branch branch : targetBranches) {
                double fitness = fitnessFunctions.get(branch).applyAsDouble(testCase);
                branchFitness.put(branch, fitness);
            }
            fitnessMap.put(testCase, branchFitness);
        }

        return fitnessMap;
    }

    private void updateArchive(List<TestCase> population, Map<TestCase, Map<Branch, Double>> fitnessMap) {
        for (TestCase testCase : population) {
            boolean dominates = true;

            Iterator<TestCase> archiveIterator = archive.iterator();
            while (archiveIterator.hasNext()) {
                TestCase archivedTestCase = archiveIterator.next();
                if (dominates(archivedTestCase, testCase, fitnessMap)) {
                    dominates = false;
                    break;
                } else if (dominates(testCase, archivedTestCase, fitnessMap)) {
                    archiveIterator.remove();
                }
            }

            if (dominates) {
                archive.add(testCase);
            }
        }
    }

    private List<List<TestCase>> nonDominatedSorting(List<TestCase> population, Map<TestCase, Map<Branch, Double>> fitnessMap) {
        List<List<TestCase>> fronts = new ArrayList<>();
        Map<TestCase, Integer> dominationCount = new HashMap<>();
        Map<TestCase, List<TestCase>> dominatedBy = new HashMap<>();

        for (TestCase p : population) {
            dominationCount.put(p, 0);
            dominatedBy.put(p, new ArrayList<>());

            for (TestCase q : population) {
                if (dominates(p, q, fitnessMap)) {
                    dominatedBy.get(p).add(q);
                } else if (dominates(q, p, fitnessMap)) {
                    dominationCount.put(p, dominationCount.get(p) + 1);
                }
            }

            if (dominationCount.get(p) == 0) {
                if (fronts.isEmpty()) fronts.add(new ArrayList<>());
                fronts.get(0).add(p);
            }
        }

        int frontIndex = 0;
        while (frontIndex < fronts.size()) {
            List<TestCase> nextFront = new ArrayList<>();
            for (TestCase p : fronts.get(frontIndex)) {
                for (TestCase q : dominatedBy.get(p)) {
                    dominationCount.put(q, dominationCount.get(q) - 1);
                    if (dominationCount.get(q) == 0) {
                        nextFront.add(q);
                    }
                }
            }
            if (!nextFront.isEmpty()) {
                fronts.add(nextFront);
            }
            frontIndex++;
        }

        return fronts;
    }

    private boolean dominates(TestCase p, TestCase q, Map<TestCase, Map<Branch, Double>> fitnessMap) {
        Map<Branch, Double> pFitness = fitnessMap.get(p);
        Map<Branch, Double> qFitness = fitnessMap.get(q);

        boolean betterInOne = false;
        for (Branch branch : targetBranches) {
            if (pFitness.get(branch) > qFitness.get(branch)) return false;
            if (pFitness.get(branch) < qFitness.get(branch)) betterInOne = true;
        }
        return betterInOne;
    }

    private void calculateSubvectorDensity(List<TestCase> front, Map<TestCase, Map<Branch, Double>> fitnessMap) {
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

    private List<TestCase> selectNextGeneration(List<List<TestCase>> fronts) {
        List<TestCase> nextGeneration = new ArrayList<>();

        for (List<TestCase> front : fronts) {
            if (nextGeneration.size() + front.size() <= populationSize) {
                nextGeneration.addAll(front);
            } else {
                front.sort(Comparator.comparingDouble(TestCase::getDensity).reversed());
                int remainingSpots = populationSize - nextGeneration.size();
                nextGeneration.addAll(front.subList(0, remainingSpots));
                break;
            }
        }

        return nextGeneration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
