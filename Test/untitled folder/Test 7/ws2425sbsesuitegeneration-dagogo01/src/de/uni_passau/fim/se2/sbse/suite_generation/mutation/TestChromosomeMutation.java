package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

import java.util.List;
import java.util.Random;

/**
 * Implements a mutation strategy for {@link TestChromosome}.
 */
public class TestChromosomeMutation implements Mutation<TestChromosome> {

    private final List<Statement> availableStatements;
    private final Random random;

    /**
     * Constructs a new mutation operator for {@link TestChromosome}.
     *
     * @param random              the random generator
     * @param availableStatements the list of available statements for mutation
     */
    public TestChromosomeMutation(Random random, List<Statement> availableStatements) {
        if (random == null || availableStatements == null) {
            throw new IllegalArgumentException("Random and availableStatements must not be null");
        }
        this.random = random;
        this.availableStatements = availableStatements;
    }

    /**
     * Applies mutation to the given chromosome and returns the resulting offspring.
     *
     * @param parent the parent chromosome to mutate
     * @return the offspring chromosome after mutation
     */
    @Override
    public TestChromosome apply(final TestChromosome parent) {
        // Create a copy of the parent chromosome
        TestChromosome offspring = parent.copy();

        // Get the list of statements in the chromosome
        List<Statement> statements = offspring.getStatements();

        // Ensure there is at least one statement to mutate
        if (statements.isEmpty()) {
            return offspring;
        }

        // Decide the type of mutation to apply
        int mutationType = random.nextInt(3); // 0: Replace, 1: Add, 2: Remove

        switch (mutationType) {
            case 0: // Replace a statement
                int replaceIndex = random.nextInt(statements.size());
                Statement newStatement = generateRandomStatement();
                statements.set(replaceIndex, newStatement);
                break;

            case 1: // Add a new statement
                Statement addedStatement = generateRandomStatement();
                statements.add(random.nextInt(statements.size() + 1), addedStatement);
                break;

            case 2: // Remove a statement
                if (statements.size() > 1) { // Ensure at least one statement remains
                    int removeIndex = random.nextInt(statements.size());
                    statements.remove(removeIndex);
                }
                break;

            default:
                throw new IllegalStateException("Unexpected mutation type: " + mutationType);
        }

        return offspring;
    }

    /**
     * Generates a random statement from the available statements.
     *
     * @return a random statement
     */
    private Statement generateRandomStatement() {
        return availableStatements.get(random.nextInt(availableStatements.size()));
    }

    @Override
    public String toString() {
        return "TestChromosomeMutation";
    }
}
