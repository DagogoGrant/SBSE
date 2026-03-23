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
    requireNonNull(population, "Population cannot be null");
    if (population.isEmpty()) {
        throw new NoSuchElementException("Population cannot be empty");
    }

    if (population.size() < TOURNAMENT_SIZE) {
        return population.get(0); // Default to the first if not enough candidates
    }

    // Select random distinct indices
    Set<Integer> selectedIndices = new HashSet<>();
    while (selectedIndices.size() < TOURNAMENT_SIZE) {
        selectedIndices.add(random.nextInt(population.size()));
    }

    // Retrieve selected chromosomes
    Iterator<Integer> iterator = selectedIndices.iterator();
    C first = population.get(iterator.next());
    C second = population.get(iterator.next());

    // Compare using the comparator; if tied, prefer the first
    int comparison = comparator.compare(first, second);
    if (comparison == 0) {
        // Deterministic tie-breaking (e.g., hash code comparison or fallback)
        return first.hashCode() <= second.hashCode() ? first : second;
    }

    return comparison > 0 ? first : second;
}

    
    
}
