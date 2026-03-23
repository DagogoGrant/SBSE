package de.uni_passau.fim.se2.sbse.suite_generation.selection;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RankSelection<C extends Chromosome<C>> implements Selection<C> {

    private final Comparator<C> comparator;
    private final int populationSize;
    private final double selectionBias;
    private final Random random;

    /**
     * Constructs a new rank selection operator.
     *
     * @param comparator the comparator for ranking individuals
     * @param size       the size of the population the selection operator should be applied to
     * @param bias       the selection bias to exercise (must be in the range [1, 2])
     * @param random     the source of randomness
     */
    public RankSelection(final Comparator<C> comparator, final int size, final double bias, final Random random) {
        if (bias < 1.0 || bias > 2.0) {
            throw new IllegalArgumentException("Selection bias must be in the range [1, 2].");
        }
        this.comparator = comparator;
        this.populationSize = size;
        this.selectionBias = bias;
        this.random = random;
    }

    /**
     * Chooses an individual from the given population using rank selection.
     *
     * @param population the population of chromosomes from which to select
     * @return the selected individual
     */
    @Override
    public C apply(final List<C> population) {
        if (population.size() != populationSize) {
            throw new IllegalArgumentException("Population size must match the fixed size: " + populationSize);
        }

        // Step 1: Sort the population by fitness (using the comparator).
        population.sort(comparator);

        // Step 2: Compute selection probabilities based on ranks.
        double[] probabilities = computeSelectionProbabilities(populationSize, selectionBias);

        // Step 3: Compute cumulative probabilities for roulette-wheel selection.
        double[] cumulativeProbabilities = new double[populationSize];
        cumulativeProbabilities[0] = probabilities[0];
        for (int i = 1; i < populationSize; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
        }

        // Step 4: Generate a random number and select an individual.
        double randomValue = random.nextDouble();
        for (int i = 0; i < populationSize; i++) {
            if (randomValue <= cumulativeProbabilities[i]) {
                return population.get(i);
            }
        }

        // Fallback in case of numerical imprecision.
        return population.get(populationSize - 1);
    }

    /**
     * Computes selection probabilities for a given population size and bias.
     *
     * @param size the population size
     * @param bias the selection bias
     * @return an array of probabilities
     */
    private double[] computeSelectionProbabilities(int size, double bias) {
        double[] probabilities = new double[size];
        double normalizationFactor = 0.0;

        // Calculate probabilities and normalization factor.
        for (int rank = 1; rank <= size; rank++) {
            double probability = (2 - bias + 2 * (bias - 1) * (rank - 1) / (size - 1)) / size;
            probabilities[rank - 1] = probability;
            normalizationFactor += probability;
        }

        // Normalize probabilities to ensure they sum to 1.
        for (int i = 0; i < size; i++) {
            probabilities[i] /= normalizationFactor;
        }

        // Ensure monotonicity.
        for (int i = 1; i < size; i++) {
            if (probabilities[i] < probabilities[i - 1]) {
                throw new IllegalStateException("Probabilities are not monotonically increasing.");
            }
        }

        return probabilities;
    }
}
