package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.utils.SelfTyped;

import java.util.Random;

public abstract class Encoding<E extends Encoding<E>> implements SelfTyped<E> {

    private final Mutation<E> mutation;
    private final Random random = new Random();

    protected Encoding(final Mutation<E> mutation) {
        this.mutation = mutation;
    }

    protected Encoding(final Encoding<E> other) {
        this.mutation = other.mutation;
    }

    public final E mutate() {
        E mutated = mutation.apply(self());
        return mutated.isValid() ? mutated : deepCopy(); // Ensure the mutated solution is valid
    }

    public abstract E deepCopy();

    public abstract int[] getPositions();

    public Mutation<E> getMutation() {
        return mutation;
    }

    public boolean isValid() {
        int[] positions = getPositions();
        boolean[] seen = new boolean[positions.length];
        for (int value : positions) {
            if (value < 0 || value >= seen.length || seen[value]) {
                return false; // Invalid if out of range or duplicate value
            }
            seen[value] = true;
        }
        return true;
    }

    // New Mutation Methods

    /**
     * Swaps two elements in the positions array randomly.
     *
     * @return the mutated encoding
     */
    public E swapMutation() {
        int[] positions = getPositions();
        int index1 = random.nextInt(positions.length);
        int index2 = random.nextInt(positions.length);

        // Swap values at index1 and index2
        int temp = positions[index1];
        positions[index1] = positions[index2];
        positions[index2] = temp;

        return isValid() ? self() : deepCopy(); // Ensuring validity after mutation
    }

    /**
     * Scrambles a subsection of the positions array.
     *
     * @return the mutated encoding
     */
    public E scrambleMutation() {
        int[] positions = getPositions();
        int startIndex = random.nextInt(positions.length);
        int endIndex = random.nextInt(positions.length);

        // Ensure startIndex is less than endIndex
        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        // Scramble elements between startIndex and endIndex
        for (int i = startIndex; i < endIndex; i++) {
            int randomIndex = startIndex + random.nextInt(endIndex - startIndex);
            int temp = positions[i];
            positions[i] = positions[randomIndex];
            positions[randomIndex] = temp;
        }

        return isValid() ? self() : deepCopy(); // Ensuring validity after mutation
    }

    /**
     * Reverses a subsection of the positions array.
     *
     * @return the mutated encoding
     */
    public E inversionMutation() {
        int[] positions = getPositions();
        int startIndex = random.nextInt(positions.length);
        int endIndex = random.nextInt(positions.length);

        // Ensure startIndex is less than endIndex
        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        // Reverse elements between startIndex and endIndex
        while (startIndex < endIndex) {
            int temp = positions[startIndex];
            positions[startIndex] = positions[endIndex];
            positions[endIndex] = temp;
            startIndex++;
            endIndex--;
        }

        return isValid() ? self() : deepCopy(); // Ensuring validity after mutation
    }
}
