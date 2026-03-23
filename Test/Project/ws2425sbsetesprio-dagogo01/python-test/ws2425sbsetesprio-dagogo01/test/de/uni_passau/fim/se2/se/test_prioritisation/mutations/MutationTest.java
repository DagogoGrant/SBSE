package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MutationTest {

    @Test
    void testMutationInstantiation() {
        // Replace `ShiftToBeginningMutation` with the actual class you want to test
        assertDoesNotThrow(() -> {
            Class<?> mutationClass = ShiftToBeginningMutation.class;
            Object mutationInstance = mutationClass.getDeclaredConstructor().newInstance();
            assertNotNull(mutationInstance, "Mutation instance should not be null.");
        });
    }
}
