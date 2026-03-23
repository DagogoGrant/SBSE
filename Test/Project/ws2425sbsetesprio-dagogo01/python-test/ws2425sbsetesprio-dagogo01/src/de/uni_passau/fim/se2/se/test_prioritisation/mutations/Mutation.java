package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

/**
 * Mutation interface for encoding transformations.
 */
public interface Mutation<E> {
    E apply(E encoding);
}
