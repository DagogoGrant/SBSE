package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

/**
 * Implementation of the Simulated Annealing algorithm.
 *
 * @param <E> the type of encoding used in this search algorithm
 */
public class SimulatedAnnealing<E extends Encoding<E>> extends SearchAlgorithm<E> {

    private final EncodingGenerator<E> generator;
    private final double initialTemperature;
    private final double coolingRate;

    /**
     * Constructs a SimulatedAnnealing algorithm.
     *
     * @param stoppingCondition   the stopping condition
     * @param generator           the encoding generator
     * @param fitnessFunction     the fitness function
     * @param initialTemperature  the initial temperature for annealing
     * @param coolingRate         the cooling rate
     */
    public SimulatedAnnealing(StoppingCondition stoppingCondition,
                              EncodingGenerator<E> generator,
                              FitnessFunction<E> fitnessFunction,
                              double initialTemperature,
                              double coolingRate) {
        super(stoppingCondition, fitnessFunction);
        this.generator = generator;
        this.initialTemperature = initialTemperature;
        this.coolingRate = coolingRate;
    }

    @Override
    public E findSolution() {
        E currentSolution = generator.generate();
        double currentFitness = getFitnessFunction().applyAsDouble(currentSolution);

        E bestSolution = currentSolution.deepCopy();
        double bestFitness = currentFitness;

        double temperature = initialTemperature;

        while (!getStoppingCondition().isFinished()) {
            E neighbor = generator.generate(); // Generate a new candidate solution
            double neighborFitness = getFitnessFunction().applyAsDouble(neighbor);

            if (acceptanceProbability(currentFitness, neighborFitness, temperature) > Math.random()) {
                currentSolution = neighbor.deepCopy();
                currentFitness = neighborFitness;
            }

            if (currentFitness > bestFitness) {
                bestSolution = currentSolution.deepCopy();
                bestFitness = currentFitness;
            }

            temperature *= coolingRate; // Cool down
            getStoppingCondition().notifyFitnessEvaluation();
        }

        return bestSolution;
    }

    /**
     * Calculates the acceptance probability.
     *
     * @param currentFitness  the current solution's fitness
     * @param neighborFitness the neighbor solution's fitness
     * @param temperature     the current temperature
     * @return the acceptance probability
     */
    private double acceptanceProbability(double currentFitness, double neighborFitness, double temperature) {
        if (neighborFitness > currentFitness) {
            return 1.0;
        }
        return Math.exp((neighborFitness - currentFitness) / temperature);
    }
}
