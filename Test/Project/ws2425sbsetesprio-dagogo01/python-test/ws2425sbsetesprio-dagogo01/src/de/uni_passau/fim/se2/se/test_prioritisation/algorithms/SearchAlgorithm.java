package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

/**
 * Abstract base class for all search algorithms.
 * 
 * @param <E> The type of encoding used in this search algorithm.
 */
public abstract class SearchAlgorithm<E extends Encoding<E>> {
    private final FitnessFunction<E> fitnessFunction;
    private final StoppingCondition stoppingCondition;

    /**
     * Constructs a search algorithm.
     *
     * @param stoppingCondition The condition to stop the search.
     * @param fitnessFunction   The fitness function to evaluate solutions.
     */
    protected SearchAlgorithm(StoppingCondition stoppingCondition, FitnessFunction<E> fitnessFunction) {
        this.stoppingCondition = stoppingCondition;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Gets the fitness function associated with the search algorithm.
     *
     * @return The fitness function.
     */
    public FitnessFunction<E> getFitnessFunction() {
        return fitnessFunction;
    }

    /**
     * Gets the stopping condition associated with the search algorithm.
     *
     * @return The stopping condition.
     */
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    /**
     * The method to find the solution. Each specific search algorithm must provide
     * its implementation.
     *
     * @return The best solution found by the search.
     */
    public abstract E findSolution();
}
