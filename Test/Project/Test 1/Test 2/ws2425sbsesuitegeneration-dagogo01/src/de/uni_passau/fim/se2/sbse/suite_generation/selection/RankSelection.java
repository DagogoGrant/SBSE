package de.uni_passau.fim.se2.sbse.suite_generation.selection;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implements rank selection for populations of a user-specified constant size. The operator works
 * as follows:
 * <ol>
 *     <li>First, the population is sorted by their fitness, according to the comparator this
 *     selection operator was constructed with.</li>
 *     <li>Then, every individual is assigned a rank, starting at rank 1 for the worst
 *     individual and finishing with the highest rank for the best individual. Note that all
 *     individuals get a different rank, even if they have the same fitness value.</li>
 *     <li>Finally, the selection probability is linearly assigned to the individuals according
 *     to their rank.</li>
 * </ol>
 * <p>
 * Rank selection overcomes some issues of fitness-proportionate (roulette-wheel) selection
 * because it is unbiased whether the fittest candidate is, for example, 10 times fitter than the
 * next fittest or just 0.1 times fitter. Furthermore, rank selection provides a degree of control
 * over selective pressure that is not possible with fitness-proportionate selection.
 * <p>
 * In particular, users of this class can choose the extent to which selective pressure is exercised
 * by specifying the so called <em>selection bias</em>. For populations of size {@code N > 1} the
 * best individual is assigned rank {@code N} and the worst individual rank {@code 1}. With this,
 * the selection probability {@code p(i)} for an individual with rank {@code 1 <= i <= N} is
 * computed as follows:
 * <pre>{@code
 * p(i) = (2 - c + 2 * (c - 1) * (i - 1) / (N - 1)) / N
 * }</pre>
 * The parameter {@code 1 < c <= 2} is the user-specified selection bias. Here, {@code p(1) = (2 -
 * c) / N} is the probability of the worst individual to be selected, and {@code p(N) = c / N} the
 * probability of the best individual to be selected. Since all individuals were assigned a
 * different rank, the selection probability is different for every individual, even for these with
 * the same fitness value. Note that once the population size has been set, the rank based selection
 * operator cannot be applied to populations of different size as the computed probabilities would
 * no longer match.
 * <p>
 * By default, when no user-specified selection bias is given, the operator assumes a sane default
 * of {@code c = 2 * N / (N + 1)}. This way,
 * <pre>{@code
 * p(1) =     2 / (N * (N + 1)) = 1 / (1 + 2 + 3 + ... + N)
 * p(i) = i * 2 / (N * (N + 1)) = i / (1 + 2 + 3 + ... + N) = i * p(1)
 * p(N) = N * 2 / (N * (N + 1)) = 2 / (N + 1)
 * }</pre>
 * This means the best individual is {@code N} times as likely to be selected as the worst
 * individual, the second best individual {@code N - 1} times as likely as the worst, and so on.
 * <p>
 * Apart from that, the operator can be used in two modes:
 * <ul>
 *     <li>In fast selection mode, the operator approximates the index {@code i} of the selected
 *     individual by transforming an equally distributed random variable {@code 0 <= r <= 1}:
 *     <pre>{@code
 *     i = floor(N * (c - Math.sqrt(c * c - 4.0 * (c - 1.0) * r)) / 2.0 / (c - 1.0));
 *     }</pre>
 *     This method has first been described and used by Whitley in the GENITOR algorithm (1989).
 *     The details are outlined in the literature. For selection biases between 1 and 2, it produces
 *     results almost identical to slow selection mode.</li>
 *     <li>In slow selection mode, the operator does not rely on approximations and has to fall back
 *     to exact (but slow) computations to determine the selection index.</li>
 * </ul>
 * @param <C> the type of chromosomes supported by this operator
 */
public class RankSelection<C extends Chromosome<C>> implements Selection<C> {

    private final Comparator<C> comparator;
    private final int populationSize;
    private final double selectionBias;
    private final Random random;
    private final boolean fastMode;

    /**
     * Constructs a new rank selection operator.
     *
     * @param comparator the comparator for ranking individuals
     * @param size       the size of the population the selection operator should be applied to
     * @param bias       the selection bias to exercise (must be in the range [1, 2])
     * @param random     the source of randomness
     * @param fastMode   whether to use fast selection mode
     */
    public RankSelection(final Comparator<C> comparator, final int size, final double bias, final Random random, final boolean fastMode) {
        if (bias < 1.0 || bias > 2.0) {
            throw new IllegalArgumentException("Selection bias must be in the range [1, 2].");
        }
        this.comparator = comparator;
        this.populationSize = size;
        this.selectionBias = bias;
        this.random = random;
        this.fastMode = fastMode;
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

        if (fastMode) {
            // Fast selection mode
            double r = random.nextDouble();
            int index = (int) Math.floor(populationSize * (selectionBias - Math.sqrt(selectionBias * selectionBias - 4.0 * (selectionBias - 1.0) * r)) / 2.0 / (selectionBias - 1.0));
            return population.get(Math.min(index, populationSize - 1));
        } else {
            // Slow selection mode
            double[] probabilities = computeSelectionProbabilities(populationSize, selectionBias);
            double[] cumulativeProbabilities = new double[populationSize];
            cumulativeProbabilities[0] = probabilities[0];
            for (int i = 1; i < populationSize; i++) {
                cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
            }

            double randomValue = random.nextDouble();
            for (int i = 0; i < populationSize; i++) {
                if (randomValue <= cumulativeProbabilities[i]) {
                    return population.get(i);
                }
            }

            return population.get(populationSize - 1); // Fallback
        }
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

        for (int rank = 1; rank <= size; rank++) {
            double probability = (2 - bias + 2 * (bias - 1) * (rank - 1) / (size - 1)) / size;
            probabilities[rank - 1] = probability;
            normalizationFactor += probability;
        }

        for (int i = 0; i < size; i++) {
            probabilities[i] /= normalizationFactor;
        }

        return probabilities;
    }
}
