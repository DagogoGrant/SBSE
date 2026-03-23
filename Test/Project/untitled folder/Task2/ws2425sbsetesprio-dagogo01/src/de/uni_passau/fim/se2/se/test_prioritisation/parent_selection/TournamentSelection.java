package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;

import java.util.*;

public class TournamentSelection implements ParentSelection<TestOrder> {

    private final int tournamentSize;
    private final APLC fitnessFunction;
    private final Random random;

    /**
     * A common default value for the size of the tournament.
     */
    private final static int DEFAULT_TOURNAMENT_SIZE = 5;

    /**
     * Creates a new tournament selection operator.
     *
     * @param tournamentSize  the size of the tournament
     * @param fitnessFunction the fitness function used to rank the test orders
     * @param random          the random generator for selection
     * @throws NullPointerException     if any of the arguments is {@code null}
     * @throws IllegalArgumentException if the tournament size is less than 1
     */
    public TournamentSelection(int tournamentSize, APLC fitnessFunction, Random random) {
        if (fitnessFunction == null || random == null) {
            throw new NullPointerException("Fitness function and random generator must not be null.");
        }
        if (tournamentSize < 1) {
            throw new IllegalArgumentException("Tournament size must be at least 1.");
        }
        this.tournamentSize = tournamentSize;
        this.fitnessFunction = fitnessFunction;
        this.random = random;
    }

    /**
     * Creates a new tournament selection operator with a default tournament size.
     *
     * @param fitnessFunction the fitness function used to rank the test orders
     * @param random          the random generator for selection
     * @throws NullPointerException if any of the arguments is {@code null}
     */
    public TournamentSelection(APLC fitnessFunction, Random random) {
        this(DEFAULT_TOURNAMENT_SIZE, fitnessFunction, random);
    }

    /**
     * Selects a single parent from a population to be evolved in the current generation of an evolutionary algorithm
     * using the tournament selection strategy.
     *
     * @param population the population from which to select parents
     * @return the selected parent
     * @throws IllegalArgumentException if the population is empty or smaller than the tournament size
     */
    @Override
    public TestOrder selectParent(List<TestOrder> population) {
        if (population == null || population.size() < tournamentSize) {
            throw new IllegalArgumentException("Population must not be null and must contain at least tournamentSize individuals.");
        }

        // If the tournament size equals the population size, return the best individual in the entire population.
        if (tournamentSize == population.size()) {
            return population.stream()
                    .max(Comparator.comparingDouble(fitnessFunction::applyAsDouble))
                    .orElseThrow(() -> new IllegalStateException("Failed to find the best individual."));
        }

        // Randomly select `tournamentSize` candidates from the population without replacement
        Set<TestOrder> tournament = new HashSet<>();
        while (tournament.size() < tournamentSize) {
            TestOrder candidate = population.get(random.nextInt(population.size()));
            tournament.add(candidate);
        }

        // Find and return the individual with the highest fitness in the tournament
        return tournament.stream()
                .max(Comparator.comparingDouble(fitnessFunction::applyAsDouble))
                .orElseThrow(() -> new IllegalStateException("Tournament selection failed to find a valid parent."));
    }
}
