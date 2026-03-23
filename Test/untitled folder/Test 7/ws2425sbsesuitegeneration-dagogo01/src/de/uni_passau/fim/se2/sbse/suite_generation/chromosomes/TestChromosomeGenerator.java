package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import java.util.ArrayList;
import java.util.List;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestChromosomeMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.TestChromosomeCrossover;
/**
 * Generates random test chromosomes for a given Class Under Test (CUT).
 */
public class TestChromosomeGenerator implements ChromosomeGenerator<TestChromosome> {

    private final Mutation<TestChromosome> mutation;
    private final Crossover<TestChromosome> crossover;
    private final Class<?> classUnderTest;

    /**
     * Constructs a new TestChromosomeGenerator with the mutation operator, crossover operator,
     * and Class Under Test (CUT).
     *
     * @param classUnderTest the Class Under Test (CUT)
     * @param mutation       the mutation operator
     * @param crossover      the crossover operator
     */
    public TestChromosomeGenerator(
        Class<?> classUnderTest,
        Mutation<TestChromosome> mutation,
        Crossover<TestChromosome> crossover
    )
    {
        if (classUnderTest == null || mutation == null || crossover == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        this.classUnderTest = classUnderTest;
        this.mutation = mutation;
        this.crossover = crossover;
    }

    /**
     * Generates a random test chromosome.
     *
     * @return a random TestChromosome
     */
    
    @Override
    public TestChromosome get() {
        List<Statement> statements = new ArrayList<>();
        List<Statement> allStatements = Utils.allStatements(classUnderTest);

        if (allStatements.isEmpty()) {
            throw new IllegalStateException("No statements found for class: " + classUnderTest.getName());
        }

        // Add the initialization statement (constructor)
        statements.add(allStatements.get(0));

        int allStatementsSize = allStatements.size();
        if (allStatementsSize > 1) {
            int maxStatements = Math.min(50, allStatementsSize); // Limit to MAX_STATEMENTS
            int numberOfStatements = Randomness.random().nextInt(1, maxStatements);

            for (int i = 1; i < numberOfStatements; i++) {
                int randomIndex = Randomness.random().nextInt(1, allStatementsSize);
                statements.add(allStatements.get(randomIndex));
            }
        }

        return new TestChromosome(mutation, crossover, statements);
    }

    /**
     * Provides a crossover operator using a fixed random generator.
     *
     * @return a crossover operator
     */
    public Crossover<TestChromosome> getCrossover() {
        return new TestChromosomeCrossover(Randomness.random());
    }

    /**
     * Provides a mutation operator using a fixed random generator.
     *
     * @return a mutation operator
     */
    public Mutation<TestChromosome> getMutation() {
        return new TestChromosomeMutation(Randomness.random(), Utils.allStatements(classUnderTest));
    }
}
