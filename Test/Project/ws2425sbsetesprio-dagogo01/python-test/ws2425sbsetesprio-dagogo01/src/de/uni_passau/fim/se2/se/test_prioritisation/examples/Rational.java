package de.uni_passau.fim.se2.se.test_prioritisation.examples;

/**
 * Represents a rational number with numerator and denominator.
 */
public class Rational {
    private final int numerator;
    private final int denominator;

    /**
     * Constructs a Rational number.
     *
     * @param numerator   the numerator
     * @param denominator the denominator (must not be zero)
     */
    public Rational(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Returns the numerator.
     *
     * @return the numerator
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Returns the denominator.
     *
     * @return the denominator
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Adds another Rational to this Rational.
     *
     * @param other the other Rational
     * @return the result of addition
     */
    public Rational add(Rational other) {
        int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator).simplify();
    }

    /**
     * Subtracts another Rational from this Rational.
     *
     * @param other the other Rational
     * @return the result of subtraction
     */
    public Rational subtract(Rational other) {
        int newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator).simplify();
    }

    /**
     * Multiplies this Rational by another Rational.
     *
     * @param other the other Rational
     * @return the result of multiplication
     */
    public Rational multiply(Rational other) {
        int newNumerator = this.numerator * other.numerator;
        int newDenominator = this.denominator * other.denominator;
        return new Rational(newNumerator, newDenominator).simplify();
    }

    /**
     * Divides this Rational by another Rational.
     *
     * @param other the other Rational
     * @return the result of division
     */
    public Rational divide(Rational other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("Cannot divide by zero.");
        }
        int newNumerator = this.numerator * other.denominator;
        int newDenominator = this.denominator * other.numerator;
        return new Rational(newNumerator, newDenominator).simplify();
    }

    /**
     * Simplifies this Rational number.
     *
     * @return the simplified Rational
     */
    public Rational simplify() {
        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        int simplifiedNumerator = numerator / gcd;
        int simplifiedDenominator = denominator / gcd;

        // Ensure denominator is positive
        if (simplifiedDenominator < 0) {
            simplifiedNumerator = -simplifiedNumerator;
            simplifiedDenominator = -simplifiedDenominator;
        }
        return new Rational(simplifiedNumerator, simplifiedDenominator);
    }

    /**
     * Computes the greatest common divisor (GCD) of two numbers.
     *
     * @param a the first number
     * @param b the second number
     * @return the GCD of the two numbers
     */
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Checks if this Rational is equal to another object.
     *
     * @param obj the other object
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rational)) return false;
        Rational other = (Rational) obj;
        Rational simplifiedThis = this.simplify();
        Rational simplifiedOther = other.simplify();
        return simplifiedThis.numerator == simplifiedOther.numerator &&
                simplifiedThis.denominator == simplifiedOther.denominator;
    }

    /**
     * Returns the string representation of this Rational.
     *
     * @return a string in the form "numerator/denominator"
     */
    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    /**
     * Calculates the decimal value of this Rational.
     *
     * @return the decimal value as a double
     */
    public double toDecimal() {
        return (double) numerator / denominator;
    }

    /**
     * Compares this Rational with another Rational.
     *
     * @param other the other Rational
     * @return -1 if this is less than other, 0 if equal, and 1 if greater
     */
    public int compareTo(Rational other) {
        int left = this.numerator * other.denominator;
        int right = other.numerator * this.denominator;
        return Integer.compare(left, right);
    }
}
