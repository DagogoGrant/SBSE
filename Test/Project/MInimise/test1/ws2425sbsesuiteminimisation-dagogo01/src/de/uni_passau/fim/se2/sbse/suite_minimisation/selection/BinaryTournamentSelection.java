package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.TestSuiteChromosome;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a binary tournament selection operator that chooses individuals without replacement.
 */
public class BinaryTournamentSelection implements Selection<TestSuiteChromosome> {

    private static final int TOURNAMENT_SIZE = 2;

    private final Random random;
    private final Comparator<TestSuiteChromosome> comparator;

    /**
     * Creates a new binary tournament selection operator without replacement.
     *
     * @param comparator Comparator to compare chromosomes.
     * @param random     Source of randomness.
     */
    public BinaryTournamentSelection(
            final Comparator<TestSuiteChromosome> comparator,
            final Random random) {
        this.random = Objects.requireNonNull(random, "Random cannot be null");
        this.comparator = Objects.requireNonNull(comparator, "Comparator cannot be null");
    }

    /**
     * Applies binary tournament selection to a population.
     *
     * @param population List of chromosomes from which to select.
     * @return The best individual selected.
     */
    @Override
public C apply(final List<C> population) throws NullPointerException, NoSuchElementException {
    if (population == null) {
        throw new NullPointerException("Population must not be null");
    }
    if (population.size() < TOURNAMENT_SIZE) {
        throw new NoSuchElementException("Population size must be at least 2");
    }
    
    // Rest of your logic for binary tournament selection
    List<C> tournament = new ArrayList<>(TOURNAMENT_SIZE);
    Collections.shuffle(population, random);

    tournament.add(population.get(0));
    tournament.add(population.get(1));
    return tournament.stream().max(comparator).orElseThrow();
}

}
