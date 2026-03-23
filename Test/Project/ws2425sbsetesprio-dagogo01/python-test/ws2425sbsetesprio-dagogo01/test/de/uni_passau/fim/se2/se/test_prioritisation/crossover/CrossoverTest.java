package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrossoverTest {

    @Test
    void testCrossoverInstantiation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Assuming the Crossover class or interface has a valid implementation to test
        Class<?> crossoverClass = OrderCrossover.class; // Replace with the class to test

        // Use the updated approach to instantiate the class
        Object crossoverInstance = crossoverClass.getDeclaredConstructor().newInstance();

        // Validate that the instance is not null
        assertNotNull(crossoverInstance, "Crossover instance should not be null.");
    }
}
