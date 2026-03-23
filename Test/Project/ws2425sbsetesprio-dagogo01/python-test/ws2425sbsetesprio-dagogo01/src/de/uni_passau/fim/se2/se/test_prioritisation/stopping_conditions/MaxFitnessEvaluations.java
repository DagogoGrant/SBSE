package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * A stopping condition that limits the maximum number of fitness evaluations.
 */
public class MaxFitnessEvaluations implements StoppingCondition {

    private final int maxEvaluations;
    private int currentEvaluations;

    /**
     * Constructs a stopping condition with a default maximum number of fitness evaluations.
     * Default value is set to 1000.
     */
    public MaxFitnessEvaluations() {
        this(1000); // Default to 1000 evaluations
    }

    /**
     * Constructs a stopping condition with a specified maximum number of fitness evaluations.
     *
     * @param maxEvaluations the maximum number of fitness evaluations
     */
    public MaxFitnessEvaluations(int maxEvaluations) {
        if (maxEvaluations <= 0) {
            throw new IllegalArgumentException("Max evaluations must be greater than zero.");
        }
        this.maxEvaluations = maxEvaluations;
        this.currentEvaluations = 0;
    }

    @Override
    public void notifySearchStarted() {
        currentEvaluations = 0; // Reset the counter at the start of the search
    }

    @Override
    public void notifyFitnessEvaluation() {
        currentEvaluations++; // Increment the counter for each evaluation
    }

    @Override
    public boolean searchMustStop() {
        return currentEvaluations >= maxEvaluations;
    }

    @Override
    public double getProgress() {
        return Math.min((double) currentEvaluations / maxEvaluations, 1.0);
    }

    @Override
    public boolean isFinished() {
        return searchMustStop(); // Shorthand for readability
    }
}
