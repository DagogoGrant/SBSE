package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * Stopping condition that stops the search after a specified number of fitness evaluations.
 */
public class MaxFitnessEvaluations implements StoppingCondition {

    /**
     * The maximum number of fitness evaluations allowed.
     */
    private final int maxFitnessEvaluations;

    /**
     * The current number of fitness evaluations performed.
     */
    private int currentFitnessEvaluations;

    /**
     * Creates a new stopping condition with the given maximum number of fitness evaluations.
     *
     * @param maxFitnessEvaluations the maximum number of fitness evaluations allowed
     * @throws IllegalArgumentException if {@code maxFitnessEvaluations} is not positive
     */
    public MaxFitnessEvaluations(final int maxFitnessEvaluations) {
        if (maxFitnessEvaluations <= 0) {
            throw new IllegalArgumentException("The maximum number of fitness evaluations must be positive.");
        }
        this.maxFitnessEvaluations = maxFitnessEvaluations;
        this.currentFitnessEvaluations = 0;  // Initialize evaluations count to zero
    }

    /**
     * Notifies that the search has started, resetting the number of fitness evaluations.
     */
    @Override
    public void notifySearchStarted() {
        this.currentFitnessEvaluations = 0;
    }

    /**
     * Notifies that a fitness evaluation has been performed, incrementing the current count.
     */
    @Override
    public void notifyFitnessEvaluation() {
        this.currentFitnessEvaluations++;
    }

    /**
     * Checks whether the search must stop based on the number of fitness evaluations performed.
     *
     * @return {@code true} if the maximum number of fitness evaluations has been reached or exceeded,
     *         {@code false} otherwise
     */
    @Override
    public boolean searchMustStop() {
        return currentFitnessEvaluations >= maxFitnessEvaluations;
    }

    /**
     * Returns the progress towards reaching the maximum number of fitness evaluations.
     * The value ranges between 0.0 and 1.0.
     *
     * @return the progress as a value between 0.0 and 1.0
     */
    @Override
    public double getProgress() {
        return (double) currentFitnessEvaluations / maxFitnessEvaluations;
    }
}
