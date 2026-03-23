package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxFitnessEvaluationsTest {

    private MaxFitnessEvaluations maxFitnessEvaluations;

    @BeforeEach
    void setUp() {
        maxFitnessEvaluations = new MaxFitnessEvaluations(5);
    }

    @Test
    void testNotifySearchStarted() {
        maxFitnessEvaluations.notifySearchStarted();
        assertFalse(maxFitnessEvaluations.searchMustStop(), "Search should not stop at the beginning.");
        assertEquals(0.0, maxFitnessEvaluations.getProgress(), "Progress should be 0 at the start.");
    }

    @Test
    void testNotifyFitnessEvaluation() {
        maxFitnessEvaluations.notifySearchStarted();

        for (int i = 0; i < 5; i++) {
            assertFalse(maxFitnessEvaluations.searchMustStop(), "Search should not stop before reaching max evaluations.");
            maxFitnessEvaluations.notifyFitnessEvaluation();
        }

        assertTrue(maxFitnessEvaluations.searchMustStop(), "Search should stop after reaching max evaluations.");
        assertEquals(1.0, maxFitnessEvaluations.getProgress(), "Progress should be 1 after all evaluations are done.");
    }

    @Test
    void testGetProgressPartial() {
        maxFitnessEvaluations.notifySearchStarted();
        maxFitnessEvaluations.notifyFitnessEvaluation();
        maxFitnessEvaluations.notifyFitnessEvaluation();

        assertEquals(0.4, maxFitnessEvaluations.getProgress(), 1e-8, "Progress should reflect partial completion correctly.");
    }

    @Test
    void testInvalidMaxFitnessEvaluations() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFitnessEvaluations(0), "Should throw IllegalArgumentException for max evaluations <= 0.");
    }

    @Test
    void testProgressBeyondLimit() {
        maxFitnessEvaluations.notifySearchStarted();

        for (int i = 0; i < 10; i++) {
            maxFitnessEvaluations.notifyFitnessEvaluation();
        }

        assertTrue(maxFitnessEvaluations.searchMustStop(), "Search should stop after exceeding max evaluations.");
        assertEquals(1.0, maxFitnessEvaluations.getProgress(), "Progress should be capped at 1.0.");
    }
}
