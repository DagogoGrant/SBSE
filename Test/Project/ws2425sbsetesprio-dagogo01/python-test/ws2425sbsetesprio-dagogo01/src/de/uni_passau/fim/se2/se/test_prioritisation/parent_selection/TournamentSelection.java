package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;

import java.util.List;
import java.util.Random;

/**
 * A class that implements tournament selection as a parent selection strategy
 * in genetic algorithms.
 *
 * @param <E> the type of encoding used in the genetic algorithm.
 */
public class TournamentSelection<E extends Encoding<E>> implements ParentSelection<E> {

    private final Random random;
    private final int tournamentSize;

    /**
     * Constructor for TournamentSelection.
     *
     * @param random         the random number generator.
     * @param tournamentSize the number of candidates to evaluate in the tournament.
     */
    public TournamentSelection(Random random, int tournamentSize) {
        if (tournamentSize <= 0) {
            throw new IllegalArgumentException("Tournament size must be greater than 0.");
        }
        this.random = random;
        this.tournamentSize = tournamentSize;
    }

    /**
     * Selects the best candidate from a randomly chosen subset of the population.
     *
     * @param population the list of candidates to choose from.
     * @return the selected candidate.
     */
    @Override
    public E select(List<E> population) {
        if (population == null || population.isEmpty()) {
            throw new IllegalArgumentException("Population must not be null or empty.");
        }
        E best = null;
        for (int i = 0; i < tournamentSize; i++) {
            E candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }
}
