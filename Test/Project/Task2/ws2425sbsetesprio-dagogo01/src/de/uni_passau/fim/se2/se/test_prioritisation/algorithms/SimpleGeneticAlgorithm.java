package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class SimpleGeneticAlgorithm<E extends Encoding<E>> implements SearchAlgorithm<E> {
    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> fitnessFunction;
    private final Crossover<E> crossover;
    private final ParentSelection<E> parentSelection;
    private final Random random;
    private List<E> population;
    private E bestSolution;
    private double bestFitness;

    public static final int POPULATION_SIZE = 100;
    private static double CROSSOVER_PROBABILITY = 0.9;

    public SimpleGeneticAlgorithm(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> fitnessFunction,
            final Crossover<E> crossover,
            final ParentSelection<E> parentSelection,
            final Random random) {
        if (stoppingCondition == null || encodingGenerator == null || fitnessFunction == null
                || crossover == null || parentSelection == null || random == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.fitnessFunction = fitnessFunction;
        this.crossover = crossover;
        this.parentSelection = parentSelection;
        this.random = random;
        this.population = new ArrayList<>();
        this.bestSolution = null;
        this.bestFitness = Double.NEGATIVE_INFINITY;
    }

    protected void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(encodingGenerator.get());
        }
    }

    protected void evaluatePopulation() {
        for (E individual : population) {
            double fitness = fitnessFunction.applyAsDouble(individual);
            stoppingCondition.notifyFitnessEvaluation();
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestSolution = individual.deepCopy();
            }
        }
    }

    protected List<E> elitism() {
        List<E> elitePopulation = new ArrayList<>();
        if (bestSolution != null) {
            elitePopulation.add(bestSolution.deepCopy());
        }
        return elitePopulation;
    }

    private double mutationRate = 0.05;  // Start with a base mutation rate

@Override
public E findSolution() {
    stoppingCondition.notifySearchStarted();

    initializePopulation();
    evaluatePopulation();

    while (!stoppingCondition.searchMustStop()) {
        List<E> newPopulation = elitism();

        while (newPopulation.size() < POPULATION_SIZE) {
            E parent1 = parentSelection.selectParent(population);
            E parent2 = parentSelection.selectParent(population);

            E offspring1 = parent1.deepCopy();
            E offspring2 = parent2.deepCopy();

            if (random.nextDouble() < CROSSOVER_PROBABILITY) {
                offspring1 = crossover.apply(parent1, parent2);
            }

            // Apply mutation with a dynamically changing mutation rate
            if (random.nextDouble() < mutationRate) {
                offspring1 = offspring1.mutate();
            }
            if (random.nextDouble() < mutationRate) {
                offspring2 = offspring2.mutate();
            }

            newPopulation.add(offspring1);
            if (newPopulation.size() < POPULATION_SIZE) {
                newPopulation.add(offspring2);
            }
        }

        population = newPopulation;
        evaluatePopulation();

        // Update mutation rate dynamically if needed
        if (stoppingCondition.getProgress() > 0.8) {
            mutationRate = 0.01;  // Reduce mutation rate as we approach convergence
        }
    }

    return bestSolution;
}


    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    public void setPopulation(List<E> population) {
        this.population = new ArrayList<>(population);
    }

    public List<E> getPopulation() {
        return this.population;
    }

    public void setCrossoverProbability(double value) {
        CROSSOVER_PROBABILITY = value;
    }
}
