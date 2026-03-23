package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Objects;
import java.util.Random;

public class OrderCrossover implements Crossover<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Creates a new order crossover operator.
     *
     * @param random the internal source of randomness; must not be null.
     */
    public OrderCrossover(final Random random) {
        this.random = Objects.requireNonNull(random, "Random must not be null");
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
        Objects.requireNonNull(parent1, "Parent1 must not be null");
        Objects.requireNonNull(parent2, "Parent2 must not be null");

        if (parent1.size() != parent2.size()) {
            throw new IllegalArgumentException("Parents must have the same length");
        }

        int size = parent1.size();
        int[] parent1Positions = parent1.getPositions();
        int[] parent2Positions = parent2.getPositions();

        int start = random.nextInt(size);
        int end = random.nextInt(size - start) + start;

        int[] offspring = new int[size];
        boolean[] visited = new boolean[size];

        // Copy segment from parent1
        for (int i = start; i <= end; i++) {
            offspring[i] = parent1Positions[i];
            visited[parent1Positions[i]] = true;
        }

        // Fill in the remaining alleles from parent2
        int offspringIndex = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int allele = parent2Positions[(end + 1 + i) % size];
            if (!visited[allele]) {
                offspring[offspringIndex] = allele;
                offspringIndex = (offspringIndex + 1) % size;
            }
        }

        // Use the mutation from one of the parents to create the new offspring
        Mutation<TestOrder> mutation = parent1.getMutation();

        return new TestOrder(mutation, offspring);
    }
}