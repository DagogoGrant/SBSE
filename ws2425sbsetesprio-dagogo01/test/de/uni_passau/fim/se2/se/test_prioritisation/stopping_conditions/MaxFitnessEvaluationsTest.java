package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaxFitnessEvaluationsTest {

    @Test
    public void testConstructor_ValidInput() {
        // Arrange & Act
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(10);

        // Assert
        assertNotNull(stoppingCondition);
    }

    @Test
    public void testConstructor_InvalidInput() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new MaxFitnessEvaluations(0));
        assertThrows(IllegalArgumentException.class, () -> new MaxFitnessEvaluations(-1));
    }

    @Test
    public void testNotifySearchStarted() {
        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(5);

        // Act
        stoppingCondition.notifySearchStarted();

        // Assert
        assertEquals(0.0, stoppingCondition.getProgress());
        assertFalse(stoppingCondition.searchMustStop());
    }

    @Test
    public void testNotifyFitnessEvaluation() {
        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(3);

        // Act
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();

        // Assert
        assertTrue(stoppingCondition.searchMustStop());
        assertEquals(1.0, stoppingCondition.getProgress());
    }

    @Test
    public void testSearchMustStop_BeforeMaxEvaluations() {
        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(4);

        // Act
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();

        // Assert
        assertFalse(stoppingCondition.searchMustStop());
    }

    @Test
    public void testSearchMustStop_AfterMaxEvaluations() {
        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(2);

        // Act
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();

        // Assert
        assertTrue(stoppingCondition.searchMustStop());
    }

    @Test
    public void testGetProgress() {
        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(5);

        // Act & Assert
        assertEquals(0.0, stoppingCondition.getProgress());

        stoppingCondition.notifyFitnessEvaluation();
        assertEquals(0.2, stoppingCondition.getProgress());

        stoppingCondition.notifyFitnessEvaluation();
        assertEquals(0.4, stoppingCondition.getProgress());

        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();

        assertEquals(1.0, stoppingCondition.getProgress());
    }
}
