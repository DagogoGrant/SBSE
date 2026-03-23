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
        requireNonNull(population, "Population must not be null");
    
        if (population.size() < TOURNAMENT_SIZE) {
            throw new NoSuchElementException("Population must contain at least two individuals for tournament selection.");
        }

    int index1 = random.nextInt(population.size());
    int index2;
    do {
        index2 = random.nextInt(population.size());
    } while (index1 == index2);

    C individual1 = population.get(index1);
    C individual2 = population.get(index2);

    System.out.println("Comparing:");
    System.out.println("Chromosome 1: " + individual1 + " Fitness: " + comparator.compare(individual1, individual2));
    System.out.println("Chromosome 2: " + individual2 + " Fitness: " + comparator.compare(individual2, individual1));

    // Return the better chromosome
    return comparator.compare(individual1, individual2) <= 0 ? individual1 : individual2;
}

}