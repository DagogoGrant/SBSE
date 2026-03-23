package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Implements a mutation strategy where one statement might be randomly replaced, added, or removed.
 */
public class TestMutation implements Mutation<TestChromosome> {

    private final Random random;
    private final List<Statement> possibleStatements;

    /**
     * Constructs a TestMutation with a Random instance and a list of possible statements to use for mutation.
     *
     * @param random the Random instance used for mutation
     * @param possibleStatements a list of statements that can be used to replace, add, or remove existing statements
     */
    public TestMutation(Random random, List<JavaStatement> possibleStatements) {
        this.random = requireNonNull(random, "Random instance must not be null");
        // Cast or adapt JavaStatement to Statement
        this.possibleStatements = new ArrayList<>(requireNonNull(possibleStatements, "List of possible statements must not be null"));
        if (this.possibleStatements.isEmpty()) {
            throw new IllegalStateException("Possible statements cannot be empty for mutation");
        }
    }

    /**
     * Applies mutation to the given TestChromosome by potentially replacing, adding, or removing statements.
     *
     * @param testChromosome the TestChromosome to mutate
     * @return a new TestChromosome with one statement potentially replaced, added, or removed
     */
    @Override
    public TestChromosome apply(final TestChromosome testChromosome) {
        requireNonNull(testChromosome, "TestChromosome to mutate must not be null");

        List<Statement> statements = testChromosome.getStatements();
        if (statements.isEmpty()) {
            // Fallback: If no statements exist, create a new chromosome with one random statement
            Statement fallbackStatement = possibleStatements.get(random.nextInt(possibleStatements.size()));
            List<Statement> fallbackStatements = List.of(fallbackStatement);
            return new TestChromosome(testChromosome.getMutation(), testChromosome.getCrossover(), fallbackStatements);
        }

        // Create a copy of the original statements to avoid modifying in-place
        List<Statement> mutatedStatements = new ArrayList<>(statements);

        int mutationType = random.nextInt(3); // Choose a mutation type: add, replace, or remove
        switch (mutationType) {
            case 0: // Add a random statement
                Statement newStatement = possibleStatements.get(random.nextInt(possibleStatements.size()));
                mutatedStatements.add(newStatement);
                break;
            case 1: // Replace a random statement
                if (!mutatedStatements.isEmpty()) {
                    int indexToReplace = random.nextInt(mutatedStatements.size());
                    Statement replacementStatement = possibleStatements.get(random.nextInt(possibleStatements.size()));
                    mutatedStatements.set(indexToReplace, replacementStatement);
                }
                break;
            case 2: // Remove a random statement
                if (!mutatedStatements.isEmpty()) {
                    int indexToRemove = random.nextInt(mutatedStatements.size());
                    mutatedStatements.remove(indexToRemove);
                }
                break;
            default:
                // Fallback: No mutation performed; return the original chromosome copy
                return testChromosome.copy();
        }

        // Return the mutated TestChromosome
        return new TestChromosome(testChromosome.getMutation(), testChromosome.getCrossover(), mutatedStatements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TestMutation (Add/Replace/Remove Statement)";
    }
}
