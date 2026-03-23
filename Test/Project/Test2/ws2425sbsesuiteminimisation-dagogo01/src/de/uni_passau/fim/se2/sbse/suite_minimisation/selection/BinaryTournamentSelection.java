package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;


import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Chromosome;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Implements a binary tournament selection operator that chooses individuals without replacement.
 *
 * @param <C> the type of chromosomes
 */
public class BinaryTournamentSelection<C extends Chromosome<C>> implements Selection<C> {

    private static final int TOURNAMENT_SIZE = 2;

    private final Random random;
    private final Comparator<C> comparator;

    /**
     * Creates a new binary tournament selection operator without replacement,
     * comparing individuals according to the given comparator.
     *
     * @param comparator for comparing chromosomes
     * @param random     the source of randomness
     * @throws NullPointerException if the comparator is null
     */
    public BinaryTournamentSelection(
            final Comparator<C> comparator,
            final Random random)
            throws NullPointerException, IllegalArgumentException {
        this.random = requireNonNull(random);
        this.comparator = requireNonNull(comparator);
    }

    /**
     * Applies binary tournament selection without replacement to the given population.
     *
     * @param population of chromosomes from which to select
     * @return the best individual in the tournament
     * @throws NullPointerException   if the population is {@code null}
     * @throws NoSuchElementException if the population is empty
     */
    @Override
public C apply(final List<C> population) throws NullPointerException, NoSuchElementException {
    // Validate input
    if (population == null) {
        throw new NullPointerException("Population cannot be null");
    }
    if (population.isEmpty()) {
        throw new NoSuchElementException("Population cannot be empty");
    }

    // Randomly select two distinct chromosomes
    int index1 = random.nextInt(population.size());
    int index2;
    do {
        index2 = random.nextInt(population.size());
    } while (index1 == index2);

    C candidate1 = population.get(index1);
    C candidate2 = population.get(index2);

    // Compare using the provided comparator
    return comparator.compare(candidate1, candidate2) > 0 ? candidate1 : candidate2;
}

}
