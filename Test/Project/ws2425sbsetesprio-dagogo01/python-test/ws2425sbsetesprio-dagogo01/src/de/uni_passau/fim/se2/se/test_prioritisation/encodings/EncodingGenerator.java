package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

/**
 * Interface for generating instances of encodings.
 *
 * @param <E> the encoding type
 */
public interface EncodingGenerator<E extends Encoding<E>> {
    E generate();
}
