package de.uni_passau.fim.se2.se.test_prioritisation.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddNumbersTest {

    @Test
    void testAddition() {
        AddNumbers addNumbers = new AddNumbers();
        int res = addNumbers.add(1, 2);
        assertEquals(3, res, "1 + 2 should equal 3");
    }

    @Test
    void testFoo() {
        AddNumbers addNumbers = new AddNumbers();
        addNumbers.add(1, 2);
        // Removed or replaced `foo(3)` since it doesn't exist.
        // Uncomment and define `foo(int)` in AddNumbers if needed.
        // addNumbers.foo(3);
    }

    // Removed empty tests if they are unnecessary
    // Uncomment or implement assertions if these tests are placeholders for future tests
    @Test
    void testPlaceholder0() {
        assertTrue(true, "Placeholder for test 0");
    }

    @Test
    void testPlaceholder1() {
        assertTrue(true, "Placeholder for test 1");
    }

    // Removed `noTest()` if unused
    // private void noTest() {
    // }
}
