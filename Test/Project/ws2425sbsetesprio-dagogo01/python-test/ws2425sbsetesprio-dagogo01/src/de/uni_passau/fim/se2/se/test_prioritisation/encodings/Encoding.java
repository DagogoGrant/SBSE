package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.utils.SelfTyped;

import java.util.Objects;

/**
 * An abstract class representing a solution encoding that can be iteratively mutated by a search algorithm
 * to find an optimal solution for a given problem.
 *
 * @param <E> the concrete type of the solution encoding
 * @apiNote Usually, it is desired that configurations of type {@code E} can only be transformed
 * into configurations of the same type {@code E}. This requirement is enforced at compile time
 * by specifying a recursive type parameter, here: {@code E extends Encoding<E>}.
 */
public abstract class Encoding<E extends Encoding<E>> implements SelfTyped<E> {

    /**
     * The mutation operator used for an elementary transformation of a solution encoding.
     */
    private final Mutation<E> mutation;

    /**
     * Constructs a new solution encoding with the specified mutation operator.
     *
     * @param mutation the mutation operator to use; must not be null
     * @throws NullPointerException if {@code mutation} is null
     */
    protected Encoding(final Mutation<E> mutation) {
        this.mutation = Objects.requireNonNull(mutation, "Mutation operator must not be null.");
    }

    /**
     * Copy constructor for creating a new instance based on an existing encoding.
     *
     * @param other the solution encoding to copy; must not be null
     * @throws NullPointerException if {@code other} is null
     */
    protected Encoding(final Encoding<E> other) {
        this(Objects.requireNonNull(other, "Other encoding must not be null.").mutation);
    }

    /**
     * Mutates the current solution encoding by performing an elementary transformation
     * using the configured mutation operator.
     *
     * @return a new mutated solution encoding of the same type
     */
    public final E mutate() {
        return mutation.apply(self());
    }

    /**
     * Creates a deep copy of the current solution encoding. Subclasses must implement this method.
     *
     * @return a deep copy of the current solution encoding
     */
    public abstract E deepCopy();

    /**
     * Returns the fitness value of this encoding. Subclasses must implement this method to provide
     * the fitness value based on the specific optimization problem.
     *
     * @return the fitness value of this encoding
     */
    public abstract double getFitness();

    /**
     * Retrieves the mutation operator associated with this encoding.
     *
     * @return the mutation operator
     */
    public Mutation<E> getMutation() {
        return mutation;
    }
}
