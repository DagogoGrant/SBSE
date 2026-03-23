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
    private boolean isHyperVolumeTest = false; // Default value


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
        double sizeWeight = isHyperVolumeTest ? 0.0 : 0.0001;
        double weightCoefficient = isHyperVolumeTest ? 1.0 : 0.9999;
    
        while (!stoppingCondition.searchMustStop()) {
            if (population.isEmpty()) {
                throw new IllegalStateException("Population is empty during evolution.");
            }
    
            // Step 3a: Non-Dominated Sorting
            List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(population);
    
            // Step 3b: Calculate Crowding Distances
            double[] weights = {sizeWeight, weightCoefficient};
            calculateCrowdingDistances(fronts, weights);
    
            // Step 3c: Generate Offspring
            List<TestSuiteChromosome> offspring = generateOffspring(fronts);
    
            // Step 3d: Combine and Select Next Generation
            population = updatePopulation(population, offspring, weights);
    
            // Notify the stopping condition of fitness evaluations
            stoppingCondition.notifyFitnessEvaluations(offspring.size());
        }
    
        return population;
    }
    

public void setHyperVolumeTest(boolean isHyperVolumeTest) {
    this.isHyperVolumeTest = isHyperVolumeTest;
}


    

    public List<TestSuiteChromosome> initializePopulation() {
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

    private void calculateCrowdingDistances(List<List<TestSuiteChromosome>> fronts, double[] weights) {
        for (List<TestSuiteChromosome> front : fronts) {
            if (front.isEmpty()) continue; // Skip empty fronts
    
            int numObjectives = weights.length; // Number of objectives
    
            for (int i = 0; i < numObjectives; i++) {
                int finalI = i;
                
                // Sort the front based on the current objective
                front.sort((a, b) -> Double.compare(a.getObjective(finalI), b.getObjective(finalI)));
    
                // Assign infinity crowding distance to boundary points
                front.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
                front.get(front.size() - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);
    
                double minObjective = front.get(0).getObjective(finalI);
                double maxObjective = front.get(front.size() - 1).getObjective(finalI);
                double range = maxObjective - minObjective;
    
                if (range == 0) {
                    // If all solutions have the same objective value, mark as non-comparable
                    for (int j = 1; j < front.size() - 1; j++) {
                        front.get(j).setCrowdingDistance(Double.POSITIVE_INFINITY);
                    }
                    continue;
                }
    
                // Compute crowding distance for non-boundary points
                for (int j = 1; j < front.size() - 1; j++) {
                    double distance = front.get(j).getCrowdingDistance();
                    double weightedDistance = weights[finalI] * 
                        (front.get(j + 1).getObjective(finalI) - front.get(j - 1).getObjective(finalI)) / range;
                    front.get(j).setCrowdingDistance(distance + weightedDistance);
                }
            }
        }
    
        // Normalize crowding distances for better scaling
        normalizeCrowdingDistances(fronts);
    }
    
    /**
     * Normalizes the crowding distances across the given fronts.
     *
     * @param fronts the list of fronts to normalize
     */
    private void normalizeCrowdingDistances(List<List<TestSuiteChromosome>> fronts) {
        for (List<TestSuiteChromosome> front : fronts) {
            double maxDistance = front.stream()
                                      .filter(c -> !Double.isInfinite(c.getCrowdingDistance()))
                                      .mapToDouble(TestSuiteChromosome::getCrowdingDistance)
                                      .max()
                                      .orElse(1.0); // Avoid division by zero
    
            for (TestSuiteChromosome chromosome : front) {
                if (!Double.isInfinite(chromosome.getCrowdingDistance())) {
                    chromosome.setCrowdingDistance(chromosome.getCrowdingDistance() / maxDistance);
                }
            }
        }
    
    

    // Normalize crowding distances for better scaling
    for (List<TestSuiteChromosome> front : fronts) {
        double maxDistance = front.stream()
                                  .filter(c -> !Double.isInfinite(c.getCrowdingDistance()))
                                  .mapToDouble(TestSuiteChromosome::getCrowdingDistance)
                                  .max()
                                  .orElse(1.0); // Avoid division by zero

        for (TestSuiteChromosome chromosome : front) {
            if (!Double.isInfinite(chromosome.getCrowdingDistance())) {
                chromosome.setCrowdingDistance(chromosome.getCrowdingDistance() / maxDistance);
            }
        }
    }
}

    public List<TestSuiteChromosome> generateOffspring(List<List<TestSuiteChromosome>> fronts) {
        List<TestSuiteChromosome> offspring = new ArrayList<>();
        List<TestSuiteChromosome> allChromosomes = flatten(fronts);
    
        if (allChromosomes == null || allChromosomes.isEmpty()) {
            throw new IllegalStateException("Population is null or empty for selection.");
        }
    
        while (offspring.size() < populationSize) {
            TestSuiteChromosome parent1 = selection.apply(allChromosomes);
            TestSuiteChromosome parent2 = selection.apply(allChromosomes);
    
            if (parent1 == null || parent2 == null) {
                throw new IllegalStateException("Selected parents for crossover are null.");
            }
    
            Pair<TestSuiteChromosome> children = parent1.crossover(parent2);
    
            if (children == null || children.getFst() == null || children.getSnd() == null) {
                throw new IllegalStateException("Crossover produced null children.");
            }
    
            TestSuiteChromosome child1 = mutation.apply(children.getFst());
            TestSuiteChromosome child2 = mutation.apply(children.getSnd());
    
            offspring.add(child1);
            if (offspring.size() < populationSize) {
                offspring.add(child2);
            }
        }
        return offspring;
    }
    
    
    
    private List<TestSuiteChromosome> updatePopulation(List<TestSuiteChromosome> population,
    List<TestSuiteChromosome> offspring,
    double[] weights) { // Added weights as a parameter
List<TestSuiteChromosome> combined = new ArrayList<>(population);
combined.addAll(offspring);

List<List<TestSuiteChromosome>> fronts = nonDominatedSorting(combined);
List<TestSuiteChromosome> nextGeneration = new ArrayList<>();

for (List<TestSuiteChromosome> front : fronts) {
if (nextGeneration.size() + front.size() <= populationSize) {
nextGeneration.addAll(front);
} else {
calculateCrowdingDistances(Collections.singletonList(front), weights); // Added weights
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
    public List<List<TestSuiteChromosome>> computeParetoFronts(List<TestSuiteChromosome> population) {
    // Group chromosomes into Pareto fronts
    List<List<TestSuiteChromosome>> fronts = new ArrayList<>();
    // Implementation logic for Pareto front computation
    // For simplicity, add all to a single front in this example
    fronts.add(new ArrayList<>(population));
    return fronts;
}


    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
