package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a test order in the genetic algorithm.
 */
public class TestOrder extends Encoding<TestOrder> {

    private final int[] positions;
    private double fitness;

    /**
     * Constructs a TestOrder with the specified positions and mutation strategy.
     *
     * @param positions the positions array representing the test order
     * @param mutation  the mutation strategy to apply
     */
    public TestOrder(int[] positions, Mutation<TestOrder> mutation) {
        super(mutation);
        if (positions == null || positions.length == 0) {
            throw new IllegalArgumentException("Positions array must not be null or empty.");
        }
        this.positions = positions.clone(); // Ensure immutability
    }

    /**
     * Creates a deep copy of this TestOrder.
     *
     * @return a new TestOrder with the same positions and mutation strategy
     */
    @Override
    public TestOrder deepCopy() {
        return new TestOrder(positions.clone(), getMutation());
    }

    /**
     * Gets the fitness value of this TestOrder.
     *
     * @return the fitness value
     */
    @Override
    public double getFitness() {
        return fitness;
    }

    /**
     * Sets the fitness value of this TestOrder.
     *
     * @param fitness the new fitness value
     */
    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns this TestOrder instance.
     *
     * @return this TestOrder
     */
    @Override
    protected TestOrder self() { // Ensure the method is protected as expected by the parent class
        return this;
    }

    /**
     * Returns the positions array representing the test order.
     *
     * @return a copy of the positions array
     */
    public int[] getPositions() {
        return positions.clone(); // Return a copy to ensure immutability
    }

    /**
     * Checks whether the positions array is valid.
     * A valid array:
     * - Contains all unique values.
     * - Contains values in the range [0, positions.length - 1].
     *
     * @return true if the positions array is valid, false otherwise.
     */
    public boolean isValid() {
        Set<Integer> seen = new HashSet<>();
        for (int pos : positions) {
            if (pos < 0 || pos >= positions.length || !seen.add(pos)) {
                return false; // Invalid if out of range or duplicate
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the TestOrder.
     *
     * @return the string representation of the positions array.
     */
    @Override
    public String toString() {
        return "TestOrder{" +
                "positions=" + Arrays.toString(positions) +
                ", fitness=" + fitness +
                '}';
    }

    /**
     * Checks for equality between this and another TestOrder.
     *
     * @param obj the other object to compare
     * @return true if the positions and fitness are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TestOrder testOrder = (TestOrder) obj;
        return Double.compare(testOrder.fitness, fitness) == 0 &&
                Arrays.equals(positions, testOrder.positions);
    }

    /**
     * Generates a hash code for the TestOrder based on positions and fitness.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(positions);
        long temp = Double.doubleToLongBits(fitness);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
