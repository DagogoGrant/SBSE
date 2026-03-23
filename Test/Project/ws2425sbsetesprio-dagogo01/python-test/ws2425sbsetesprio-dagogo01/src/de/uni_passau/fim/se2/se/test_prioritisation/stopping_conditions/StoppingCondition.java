package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * Interface for defining conditions that stop a search algorithm.
 */
public interface StoppingCondition {
    /**
     * Notifies that the search process has started.
     */
    void notifySearchStarted();

    /**
     * Notifies that a fitness evaluation has occurred.
     */
    void notifyFitnessEvaluation();

    /**
     * Checks whether the search must stop.
     *
     * @return {@code true} if the search must stop, otherwise {@code false}
     */
    boolean searchMustStop();

    /**
     * Gets the progress of the search as a fraction between 0 and 1.
     *
     * @return the progress of the search
     */
    double getProgress();

    /**
     * A shorthand for {@link #searchMustStop()} to improve readability in algorithms.
     *
     * @return {@code true} if the search is finished, otherwise {@code false}
     */
    default boolean isFinished() {
        return searchMustStop();
    }
}
