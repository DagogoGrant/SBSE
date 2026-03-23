package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxFitnessEvaluationsTest {

    @Test
    void testSearchMustStop() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(5);
        for (int i = 0; i < 5; i++) {
            assertFalse(condition.searchMustStop(), "Condition should not indicate stop before reaching the maximum.");
            condition.notifyFitnessEvaluation();
        }
        assertTrue(condition.searchMustStop(), "Condition should indicate stop after reaching the maximum.");
    }

    @Test
    void testNotifyFitnessEvaluation() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(3);
        condition.notifyFitnessEvaluation();
        assertFalse(condition.searchMustStop(), "Condition should not indicate stop after one evaluation.");
        condition.notifyFitnessEvaluation();
        condition.notifyFitnessEvaluation();
        assertTrue(condition.searchMustStop(), "Condition should indicate stop after reaching the maximum.");
    }

    @Test
    void testNotifySearchStarted() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(3);
        condition.notifyFitnessEvaluation();
        condition.notifyFitnessEvaluation();
        condition.notifySearchStarted(); // Reset the state
        assertFalse(condition.searchMustStop(), "Condition should reset and not indicate stop after search restart.");
    }

    @Test
    void testGetProgress() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(4);
        assertEquals(0.0, condition.getProgress(), "Progress should be 0.0 before any evaluations.");
        condition.notifyFitnessEvaluation();
        assertEquals(0.25, condition.getProgress(), "Progress should reflect 1/4 evaluations.");
        condition.notifyFitnessEvaluation();
        assertEquals(0.5, condition.getProgress(), "Progress should reflect 2/4 evaluations.");
        condition.notifyFitnessEvaluation();
        assertEquals(0.75, condition.getProgress(), "Progress should reflect 3/4 evaluations.");
        condition.notifyFitnessEvaluation();
        assertEquals(1.0, condition.getProgress(), "Progress should be 1.0 after all evaluations.");
    }
}
