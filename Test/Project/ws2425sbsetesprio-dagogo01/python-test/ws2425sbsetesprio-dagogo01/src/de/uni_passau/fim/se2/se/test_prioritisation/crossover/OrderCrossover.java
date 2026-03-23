package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements the order crossover operator for TestOrder encodings.
 */
public class OrderCrossover implements Crossover<TestOrder> {

    @Override
    public TestOrder apply(TestOrder parent1, TestOrder parent2) {
        int[] positions1 = parent1.getPositions();
        int[] positions2 = parent2.getPositions();

        // Select random start and end indices for crossover range
        int start = (int) (Math.random() * positions1.length);
        int end = start + (int) (Math.random() * (positions1.length - start));

        int[] childPositions = new int[positions1.length];
        Set<Integer> used = new HashSet<>();

        // Copy the range from the first parent
        for (int i = start; i <= end; i++) {
            childPositions[i] = positions1[i];
            used.add(positions1[i]);
        }

        // Fill the remaining positions from the second parent
        int currentIndex = (end + 1) % positions1.length;
        for (int i = 0; i < positions2.length; i++) {
            int candidate = positions2[i];
            if (!used.contains(candidate)) {
                childPositions[currentIndex] = candidate;
                currentIndex = (currentIndex + 1) % positions1.length;
            }
        }

        return new TestOrder(childPositions, parent1.getMutation());
    }
}
