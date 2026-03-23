package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ParentSelectionTest {

    @Test
    void testParentSelectionInstantiation() {
        try {
            // Correct instantiation of TournamentSelection with required arguments
            Random random = new Random();
            int tournamentSize = 3; // Example tournament size
            TournamentSelection<?> parentSelectionInstance = new TournamentSelection<>(random, tournamentSize);

            assertNotNull(parentSelectionInstance, "Parent selection instance should not be null.");
        } catch (Exception e) {
            fail("Exception occurred during instantiation: " + e.getMessage());
        }
    }

    @Test
    void testInvalidInstantiation() {
        class NoAccessibleConstructor {
            private NoAccessibleConstructor() {}
        }

        try {
            // Attempt to instantiate a class with a private constructor
            Constructor<?> constructor = NoAccessibleConstructor.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Allow private constructor invocation
            constructor.newInstance();

            fail("Expected an exception for classes with no accessible constructor.");
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException expected) {
            assertNotNull(expected, "Exception was expected and caught successfully.");
        }
    }
}
