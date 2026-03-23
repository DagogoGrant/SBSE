package de.uni_passau.fim.se2.se.test_prioritisation.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RationalTest {

    @Test
    void testAddition() {
        Rational r1 = new Rational(1, 2);
        Rational r2 = new Rational(1, 3);
        Rational result = r1.add(r2);
        assertEquals(new Rational(5, 6), result, "Addition result is incorrect.");
    }

    @Test
    void testSubtraction() {
        Rational r1 = new Rational(3, 4);
        Rational r2 = new Rational(1, 2);
        Rational result = r1.subtract(r2);
        assertEquals(new Rational(1, 4), result, "Subtraction result is incorrect.");
    }

    @Test
    void testMultiplication() {
        Rational r1 = new Rational(2, 3);
        Rational r2 = new Rational(3, 4);
        Rational result = r1.multiply(r2);
        assertEquals(new Rational(1, 2), result, "Multiplication result is incorrect.");
    }

    @Test
    void testDivision() {
        Rational r1 = new Rational(3, 4);
        Rational r2 = new Rational(2, 5);
        Rational result = r1.divide(r2);
        assertEquals(new Rational(15, 8), result, "Division result is incorrect.");
    }

    @Test
    void testSimplify() {
        Rational r = new Rational(4, 8);
        assertEquals(new Rational(1, 2), r.simplify(), "Simplification is incorrect.");
    }

    @Test
    void testEquality() {
        Rational r1 = new Rational(2, 4);
        Rational r2 = new Rational(1, 2);
        assertEquals(r1, r2, "Equality check failed for equivalent fractions.");
    }

    @Test
    void testInvalidDenominator() {
        assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0), "Denominator of zero should throw an exception.");
    }

    @Test
    void testDivisionByZero() {
        Rational r1 = new Rational(1, 2);
        Rational r2 = new Rational(0, 1);
        assertThrows(ArithmeticException.class, () -> r1.divide(r2), "Division by zero should throw an exception.");
    }

    @Test
    void testNegativeSimplify() {
        Rational r = new Rational(-2, -4);
        assertEquals(new Rational(1, 2), r.simplify(), "Simplification failed for negative numerator and denominator.");
    }

    @Test
    void testMixedSignsSimplify() {
        Rational r = new Rational(-2, 4);
        assertEquals(new Rational(-1, 2), r.simplify(), "Simplification failed for mixed signs.");
    }

    @Test
    void testToString() {
        Rational r = new Rational(1, 2);
        assertEquals("1/2", r.toString(), "String representation is incorrect.");
    }

    @Test
    void testDecimalValue() {
        Rational r = new Rational(1, 4);
        assertEquals(0.25, r.toDecimal(), 1e-9, "Decimal conversion is incorrect.");
    }

    @Test
    void testCompareToGreater() {
        Rational r1 = new Rational(3, 4);
        Rational r2 = new Rational(1, 2);
        assertTrue(r1.compareTo(r2) > 0, "Comparison failed: r1 should be greater than r2.");
    }

    @Test
    void testCompareToLess() {
        Rational r1 = new Rational(1, 3);
        Rational r2 = new Rational(2, 3);
        assertTrue(r1.compareTo(r2) < 0, "Comparison failed: r1 should be less than r2.");
    }

    @Test
    void testCompareToEqual() {
        Rational r1 = new Rational(2, 4);
        Rational r2 = new Rational(1, 2);
        assertTrue(r1.compareTo(r2) == 0, "Comparison failed: r1 should be equal to r2.");
    }
}
