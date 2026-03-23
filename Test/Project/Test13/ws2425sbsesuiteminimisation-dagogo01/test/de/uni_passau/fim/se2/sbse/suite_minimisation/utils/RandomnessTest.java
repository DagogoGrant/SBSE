package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomnessTest {

    @Test
    void testRandomInstanceConsistency() {
        // Obtain two instances from the Randomness class
        Random random1 = Randomness.random();
        Random random2 = Randomness.random();

        // Assert that both instances are the same
        assertSame(random1, random2, "The random instances should be the same.");
    }

    @Test
    void testRandomInstanceFunctionality() {
        // Obtain a Random instance and generate two numbers
        Random random = Randomness.random();
        int number1 = random.nextInt();
        int number2 = random.nextInt();

        // Assert that the numbers are valid integers
        assertNotEquals(number1, number2, "Consecutive random numbers should not be the same.");
    }

    // @Test
    // void testNoInstantiation() {
    //     // Attempt to access the private constructor via reflection
    //     Constructor<?> constructor = null;
    //     try {
    //         constructor = Randomness.class.getDeclaredConstructor();
    //         constructor.setAccessible(true); // Bypass private access
    //         constructor.newInstance(); // Attempt to instantiate
    //         fail("Randomness class should not be instantiable.");
    //     } catch (NoSuchMethodException | IllegalAccessException e) {
    //         fail("An unexpected exception occurred: " + e.getMessage());
    //     } catch (InvocationTargetException | InstantiationException e) {
    //         // Expected outcome when attempting to invoke the private constructor
    //         assertTrue(e.getCause() instanceof IllegalStateException,
    //             "Instantiation of Randomness should throw an IllegalStateException.");
    //     } finally {
    //         if (constructor != null) {
    //             constructor.setAccessible(false); // Reset accessibility for safety
    //         }
    //     }
    // }
}
