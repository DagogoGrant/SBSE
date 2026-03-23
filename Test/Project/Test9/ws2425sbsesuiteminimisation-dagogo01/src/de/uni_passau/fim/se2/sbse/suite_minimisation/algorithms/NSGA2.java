package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NSGA2 implements GeneticAlgorithm<TestSuiteChromosome> {

    private final StoppingCondition stoppingCondition;
    private final Mutation<TestSuiteChromosome> mutation;
    private final Crossover<TestSuiteChromosome> crossover;
    private final BinaryTournamentSelection<TestSuiteChromosome> selection;
    private final FitnessFunction<TestSuiteChromosome> sizeFitnessFunction;
    private final FitnessFunction<TestSuiteChromosome> coverageFitnessFunction;
    private final int populationSize;
    private final TestSuiteChromosomeGenerator generator;

    public NSGA2(
            StoppingCondition stoppingCondition,
            Mutation<TestSuiteChromosome> mutation,
            Crossover<TestSuiteChromosome> crossover,
            BinaryTournamentSelection<TestSuiteChromosome> selection,
            FitnessFunction<TestSuiteChromosome> sizeFitnessFunction,
            FitnessFunction<TestSuiteChromosome> coverageFitnessFunction,
            int populationSize,
            TestSuiteChromosomeGenerator generator) {

        if (populationSize <= 0) {
            throw new IllegalArgumentException("Population size must be greater than zero.");
        }
        if (generator == null) {
            throw new IllegalArgumentException("Chromosome generator cannot be null.");
        }

        this.stoppingCondition = stoppingCondition;
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.sizeFitnessFunction = sizeFitnessFunction;
        this.coverageFitnessFunction = coverageFitnessFunction;
        this.populationSize = populationSize;
        this.generator = generator;
    }

    @Override
    public List<TestSuiteChromosome> findSolution() {
        // Step 1: Initialize population
        List<TestSuiteChromosome> population = initializePopulation();

        // Step 2: Evaluate fitness
        evaluateFitness(population);

        // Step 3: Iteratively improve the population
        while (!stoppingCondition.searchMustStop()) {
            if (population.isEmpty()) {
                throw new IllegalStateException("Population is empty during evolution.");
            }

            // Step 3a: Non-Dominated Sorting
            List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(population);

            // Step 3b: Calculate Crowding Distances
            calculateCrowdingDistances(fronts);

            // Step 3c: Generate Offspring
            List<TestSuiteChromosome> offspring = generateOffspring(fronts);

            // Step 3d: Combine and Select Next Generation
            population = updatePopulation(population, offspring);

            // Notify the stopping condition of fitness evaluations
            stoppingCondition.notifyFitnessEvaluations(offspring.size());
        }

        return population;
    }

    private List<TestSuiteChromosome> initializePopulation() {
        List<TestSuiteChromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            TestSuiteChromosome chromosome = generator.get();
            if (chromosome == null) {
                throw new IllegalStateException("Chromosome generator produced a null chromosome.");
            }
            population.add(chromosome);
        }
        if (population.isEmpty()) {
            throw new IllegalStateException("Population is empty after initialization.");
        }
        return population;
    }

    private void evaluateFitness(List<TestSuiteChromosome> population) {
        for (TestSuiteChromosome chromosome : population) {
            double sizeFitness = sizeFitnessFunction.applyAsDouble(chromosome);
            double coverageFitness = coverageFitnessFunction.applyAsDouble(chromosome);

            chromosome.setObjective(0, sizeFitness);
            chromosome.setObjective(1, coverageFitness);
        }
    }

    private List<List<TestSuiteChromosome>> nonDominatedSorting(List<TestSuiteChromosome> population) {
        // Placeholder for sorting logic
        // Ensure a non-empty set of fronts
        List<List<TestSuiteChromosome>> fronts = new ArrayList<>();
        fronts.add(new ArrayList<>(population));
        return fronts;
    }

    private void calculateCrowdingDistances(List<List<TestSuiteChromosome>> fronts) {
        for (List<TestSuiteChromosome> front : fronts) {
            if (front.isEmpty()) continue;

            for (int i = 0; i < 2; i++) { // Assume 2 objectives
                int finalI = i;
                front.sort((a, b) -> Double.compare(a.getObjective(finalI), b.getObjective(finalI)));

                front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
                front.get(front.size() - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

                double minObjective = front.get(0).getObjective(finalI);
                double maxObjective = front.get(front.size() - 1).getObjective(finalI);
                double range = maxObjective - minObjective;

                for (int j = 1; j < front.size() - 1; j++) {
                    double distance = front.get(j).getCrowdingDistance();
                    distance += (front.get(j + 1).getObjective(finalI) - front.get(j - 1).getObjective(finalI)) / range;
                    front.get(j).setCrowdingDistance(distance);
                }
            }
        }
    }

    private List<TestSuiteChromosome> generateOffspring(List<List<TestSuiteChromosome>> fronts) {
        List<TestSuiteChromosome> offspring = new ArrayList<>();
        List<TestSuiteChromosome> allChromosomes = flatten(fronts);

        if (allChromosomes.isEmpty()) {
            throw new IllegalStateException("No chromosomes available for selection.");
        }

        while (offspring.size() < populationSize) {
            TestSuiteChromosome parent1 = selection.apply(allChromosomes);
            TestSuiteChromosome parent2 = selection.apply(allChromosomes);

            Pair<TestSuiteChromosome> children = parent1.crossover(parent2);

            TestSuiteChromosome child1 = children.getFst().mutate();
            TestSuiteChromosome child2 = children.getSnd().mutate();

            offspring.add(child1);
            if (offspring.size() < populationSize) {
                offspring.add(child2);
            }
        }
        return offspring;
    }

    private List<TestSuiteChromosome> updatePopulation(List<TestSuiteChromosome> population,
                                                       List<TestSuiteChromosome> offspring) {
        List<TestSuiteChromosome> combined = new ArrayList<>(population);
        combined.addAll(offspring);

        List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(combined);
        List<TestSuiteChromosome> nextGeneration = new ArrayList<>();

        for (List<TestSuiteChromosome> front : fronts) {
            if (nextGeneration.size() + front.size() <= populationSize) {
                nextGeneration.addAll(front);
            } else {
                calculateCrowdingDistances(Collections.singletonList(front));
                front.sort((a, b) -> Double.compare(b.getCrowdingDistance(), a.getCrowdingDistance()));
                nextGeneration.addAll(front.subList(0, populationSize - nextGeneration.size()));
                break;
            }
        }
        return nextGeneration;
    }

    private List<TestSuiteChromosome> flatten(List<List<TestSuiteChromosome>> fronts) {
        List<TestSuiteChromosome> all = new ArrayList<>();
        for (List<TestSuiteChromosome> front : fronts) {
            all.addAll(front);
        }
        return all;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
