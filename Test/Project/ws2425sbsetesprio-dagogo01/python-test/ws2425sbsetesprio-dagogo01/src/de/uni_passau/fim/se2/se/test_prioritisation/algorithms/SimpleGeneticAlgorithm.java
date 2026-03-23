package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Simple Genetic Algorithm.
 *
 * @param <E> the type of encoding used in this search algorithm
 */
public class SimpleGeneticAlgorithm<E extends Encoding<E>> extends SearchAlgorithm<E> {

    private final EncodingGenerator<E> generator;
    private final ParentSelection<E> parentSelection;
    private final Crossover<E> crossover;

    /**
     * Constructs a SimpleGeneticAlgorithm.
     *
     * @param stoppingCondition the stopping condition
     * @param generator         the encoding generator
     * @param fitnessFunction   the fitness function
     * @param parentSelection   the parent selection strategy
     * @param crossover         the crossover operator
     */
    public SimpleGeneticAlgorithm(StoppingCondition stoppingCondition,
                                  EncodingGenerator<E> generator,
                                  FitnessFunction<E> fitnessFunction,
                                  ParentSelection<E> parentSelection,
                                  Crossover<E> crossover) {
        super(stoppingCondition, fitnessFunction);
        this.generator = generator;
        this.parentSelection = parentSelection;
        this.crossover = crossover;
    }

    @Override
    public E findSolution() {
        // Generate initial population
        List<E> population = new ArrayList<>();
        population.add(generator.generate());
        population.add(generator.generate());

        while (!getStoppingCondition().isFinished()) {
            // Select parents
            E parent1 = parentSelection.select(population);
            E parent2 = parentSelection.select(population);

            // Perform crossover
            E offspring = crossover.apply(parent1, parent2);

            // Evaluate fitness
            double offspringFitness = getFitnessFunction().applyAsDouble(offspring);
            double parent1Fitness = getFitnessFunction().applyAsDouble(parent1);

            // Replace parent with offspring if better
            if (offspringFitness > parent1Fitness) {
                population.set(0, offspring);
            }

            getStoppingCondition().notifyFitnessEvaluation();
        }

        // Return the best individual
        return population.get(0);
    }
}
