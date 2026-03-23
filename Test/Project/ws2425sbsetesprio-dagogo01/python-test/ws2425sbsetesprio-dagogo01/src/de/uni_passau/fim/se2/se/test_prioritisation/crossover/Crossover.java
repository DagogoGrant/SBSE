package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;

/**
 * Interface for crossover operators in genetic algorithms.
 *
 * @param <E> the type of encoding
 */
public interface Crossover<E extends Encoding<E>> {
    /**
     * Apply crossover to two parents to produce an offspring.
     *
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return the offspring
     */
    E apply(E parent1, E parent2);
}
