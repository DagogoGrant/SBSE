package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosomeGenerator;

import java.util.List;
import java.util.Random;

/**
 * Implements a refined mutation strategy for {@link TestChromosome}.
 */
public class TestChromosomeMutation implements Mutation<TestChromosome> {

    private static final int MAX_STATEMENTS = 50; // Limit the number of statements per chromosome
    private TestChromosomeGenerator generator;
    private final Random random;

    /**
     * Constructs a new mutation operator for {@link TestChromosome}.
     *
     * @param generator the generator used to create new random statements
     */
    public TestChromosomeMutation(TestChromosomeGenerator generator) {
        this.generator = generator;
        this.random = new Random();
    }

    /**
     * Sets the generator for this mutation operator.
     *
     * @param generator the new generator to use
     */
    public void setGenerator(TestChromosomeGenerator generator) {
        this.generator = generator;
    }

    /**
     * Applies mutation to the given chromosome and returns the resulting offspring.
     *
     * @param parent the parent chromosome to mutate
     * @return the offspring chromosome after mutation
     */
    @Override
    public TestChromosome apply(final TestChromosome parent) {
        System.out.println("[DEBUG] Applying mutation to chromosome");
        TestChromosome offspring = parent.copy();

        List<Statement> statements = offspring.getStatements();

        if (statements.isEmpty()) {
            System.err.println("[WARNING] Chromosome has no statements to mutate");
            return offspring;
        }

        try {
            int mutationType = random.nextInt(3); // 0: Replace, 1: Add, 2: Remove
            switch (mutationType) {
                case 0: // Replace a statement
                    System.out.println("[DEBUG] Replacing a statement");
                    int replaceIndex = random.nextInt(statements.size());
                    Statement newStatement = generateRandomStatement();
                    statements.set(replaceIndex, newStatement);
                    break;
                case 1: // Add a new statement
                    if (statements.size() < MAX_STATEMENTS) {
                        System.out.println("[DEBUG] Adding a new statement");
                        Statement addedStatement = generateRandomStatement();
                        statements.add(random.nextInt(statements.size() + 1), addedStatement);
                    } else {
                        System.out.println("[DEBUG] Maximum number of statements reached");
                    }
                    break;
                case 2: // Remove a statement
                    if (statements.size() > 1) {
                        System.out.println("[DEBUG] Removing a statement");
                        int removeIndex = random.nextInt(statements.size());
                        statements.remove(removeIndex);
                    } else {
                        System.out.println("[DEBUG] Only one statement remains, skipping removal");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected mutation type: " + mutationType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] Mutation failed: " + e.getMessage());
        }

        return offspring;
    }

    /**
     * Generates a random statement using the generator.
     *
     * @return a random statement
     */
    private Statement generateRandomStatement() {
        TestChromosome tempChromosome = generator.get();
        return tempChromosome.getStatements().get(random.nextInt(tempChromosome.size()));
    }

    @Override
    public String toString() {
        return "TestChromosomeMutation";
    }
}
