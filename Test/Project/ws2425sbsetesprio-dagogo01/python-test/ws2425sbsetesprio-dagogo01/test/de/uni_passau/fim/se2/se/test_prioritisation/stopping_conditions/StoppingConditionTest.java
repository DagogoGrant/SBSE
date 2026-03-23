package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StoppingConditionTest {

    @Test
    void testStoppingConditionInstantiation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Replace `StoppingConditionImplementation` with the actual class name you want to test
        Class<?> stoppingConditionClass = MaxFitnessEvaluations.class; // Example class; adjust as necessary

        // Use the updated method to instantiate the class
        Object stoppingConditionInstance = stoppingConditionClass.getDeclaredConstructor().newInstance();

        // Validate that the instance is not null
        assertNotNull(stoppingConditionInstance, "Stopping condition instance should not be null.");
    }
}
