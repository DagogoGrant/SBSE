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

    public static final int POPULATION_SIZE = 100; // Adjusted population size
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

                // Apply crossover based on the probability
                if (random.nextDouble() < CROSSOVER_PROBABILITY) {
                    E offspring = crossover.apply(parent1, parent2);
                    newPopulation.add(offspring);
                } else {
                    // If no crossover, add deep copies of the parents
                    newPopulation.add(parent1.deepCopy());
                    if (newPopulation.size() < POPULATION_SIZE) {
                        newPopulation.add(parent2.deepCopy());
                    }
                }

                // Ensure the new population doesn't exceed the size limit
                if (newPopulation.size() >= POPULATION_SIZE) {
                    break;
                }
            }

            // Apply mutation to the new population
            List<E> mutatedPopulation = new ArrayList<>();
            for (E individual : newPopulation) {
                mutatedPopulation.add(individual.mutate());
            }

            population = mutatedPopulation;
            evaluatePopulation();
        }

        return bestSolution;
    }

    /**
     * Sets the population for the genetic algorithm.
     * This method can be used for testing or initializing the population with specific individuals.
     *
     * @param population the new population to set
     * @throws IllegalArgumentException if the population is null or has a size different from the defined POPULATION_SIZE
     */
    public void setPopulation(List<E> population) {
        if (population == null || population.size() != POPULATION_SIZE) {
            throw new IllegalArgumentException("Population must not be null and must have a size of " + POPULATION_SIZE);
        }
        this.population = new ArrayList<>(population); // Use a copy to avoid external modifications
    }

    public void setCrossoverProbability(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Crossover probability must be between 0 and 1.");
        }
        CROSSOVER_PROBABILITY = value;
    }

    public List<E> getPopulation() {
        return new ArrayList<>(this.population); // Return a copy to ensure immutability
    }

    public void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(encodingGenerator.get());
        }
    }

    public void evaluatePopulation() {
        for (E individual : population) {
            double fitness = fitnessFunction.applyAsDouble(individual);
            stoppingCondition.notifyFitnessEvaluation();
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestSolution = individual.deepCopy();
            }
        }
    }

    public List<E> elitism() {
        List<E> elitePopulation = new ArrayList<>();
        if (bestSolution != null) {
            elitePopulation.add(bestSolution.deepCopy());
        }
        return elitePopulation;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
