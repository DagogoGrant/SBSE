package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

public class CustomStoppingCondition implements StoppingCondition {
    @Override
    public void notifySearchStarted() {
        // Implement logic
    }

    @Override
    public void notifyFitnessEvaluation() {
        // Implement logic
    }

    @Override
    public boolean searchMustStop() {
        // Implement stopping logic
        return false; // Placeholder
    }

    @Override
    public double getProgress() {
        // Return the progress of the search
        return 0.0; // Placeholder
    }

    // No-argument constructor
    public CustomStoppingCondition() {
        // Initialization logic if needed
    }
}
