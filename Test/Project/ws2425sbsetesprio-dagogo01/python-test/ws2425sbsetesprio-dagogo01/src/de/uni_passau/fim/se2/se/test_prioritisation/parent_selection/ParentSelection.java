package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import java.util.List;

/**
 * Interface for parent selection strategies in genetic algorithms.
 *
 * @param <E> the type of encoding used in the genetic algorithm.
 */
public interface ParentSelection<E extends Encoding<E>> {

    /**
     * Selects a parent from the population using the specific parent selection strategy.
     *
     * @param population the list of potential parents. Must not be null or empty.
     * @return the selected parent.
     * @throws IllegalArgumentException if the population is null or empty.
     */
    E select(List<E> population);
}
