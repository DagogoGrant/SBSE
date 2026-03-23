package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * Stopping condition that stops the search after a specified number of fitness evaluations.
 */
public class MaxFitnessEvaluations implements StoppingCondition {

    private final int maxFitnessEvaluations;
    private int currentEvaluations;

    /**
     * Creates a new stopping condition with the specified maximum number of fitness evaluations.
     *
     * @param maxFitnessEvaluations the maximum number of fitness evaluations allowed
     */
    public MaxFitnessEvaluations(final int maxFitnessEvaluations) {
        if (maxFitnessEvaluations <= 0) {
            throw new IllegalArgumentException("Maximum fitness evaluations must be greater than zero.");
        }
        this.maxFitnessEvaluations = maxFitnessEvaluations;
        this.currentEvaluations = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifySearchStarted() {
        this.currentEvaluations = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyFitnessEvaluation() {
        currentEvaluations++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean searchMustStop() {
        return currentEvaluations >= maxFitnessEvaluations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getProgress() {
        return Math.min(1.0, (double) currentEvaluations / maxFitnessEvaluations);
    }
}
