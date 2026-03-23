package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.*;

public class OrderCrossover implements Crossover<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Creates a new order crossover operator.
     *
     * @param random the internal source of randomness
     */
    public OrderCrossover(final Random random) {
        this.random = random;
    }

    /**
     * Combines two parent encodings to create a new offspring encoding using the order crossover operation.
     * The order crossover corresponds to a two-point crossover where the section between two random indices is copied
     * from the first parent and the remaining alleles are added in the order they appear in the second parent.
     * The resulting children must correspond to a valid test order encoding of size n that represents a permutation of tests
     * where each test value in the range [0, n-1] appears exactly once.
     *
     * @param parent1 the first parent encoding
     * @param parent2 the second parent encoding
     * @return the offspring encoding
     */
    @Override
    public TestOrder apply(TestOrder parent1, TestOrder parent2) {
        int[] parent1Positions = parent1.getPositions();
        int[] parent2Positions = parent2.getPositions();
        int length = parent1Positions.length;

        if (length != parent2Positions.length) {
            throw new IllegalArgumentException("Parents must have the same length.");
        }

        // Randomly choose two distinct crossover points
        int crossoverPoint1 = random.nextInt(length);
        int crossoverPoint2;
        do {
            crossoverPoint2 = random.nextInt(length);
        } while (crossoverPoint1 == crossoverPoint2);

        // Ensure crossoverPoint1 < crossoverPoint2
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }

        // Create child array and fill with -1 to indicate empty spots
        int[] child = new int[length];
        Arrays.fill(child, -1);

        // Copy segment from parent1 to child
        System.arraycopy(parent1Positions, crossoverPoint1, child, crossoverPoint1, crossoverPoint2 - crossoverPoint1 + 1);

        // Fill remaining positions from parent2
        int childIndex = (crossoverPoint2 + 1) % length;
        for (int i = 0; i < length; i++) {
            int value = parent2Positions[(crossoverPoint2 + 1 + i) % length];
            if (!contains(child, value)) {
                child[childIndex] = value;
                childIndex = (childIndex + 1) % length;
            }
        }

        return new TestOrder(parent1.getMutation(), child);
    }

    /**
     * Checks if the array contains a given value.
     *
     * @param array the array to check
     * @param value the value to find
     * @return {@code true} if the value is found, {@code false} otherwise
     */
    private boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
}
