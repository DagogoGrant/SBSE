package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of a one-point crossover for TestChromosome instances.
 * This crossover operator combines the statements of two parent chromosomes
 * to produce two offspring chromosomes with mixed genetic material.
 */
public class TestCrossover implements Crossover<TestChromosome> {

    private final Random random;

    /**
     * Constructs a TestCrossover with the given Random instance for selecting the crossover point.
     *
     * @param random the Random instance used for crossover point selection
     */
    public TestCrossover(Random random) {
        this.random = requireNonNull(random, "Random instance must not be null");
    }

    /**
     * Applies one-point crossover to two parent TestChromosomes to produce two offspring TestChromosomes.
     *
     * @param parent1 the first parent TestChromosome
     * @param parent2 the second parent TestChromosome
     * @return a pair of new TestChromosome instances formed by crossover
     */
    @Override
    public Pair<TestChromosome> apply(final TestChromosome parent1, final TestChromosome parent2) {
        requireNonNull(parent1, "First parent must not be null");
        requireNonNull(parent2, "Second parent must not be null");

        List<Statement> statements1 = parent1.getStatements();
        List<Statement> statements2 = parent2.getStatements();

        int minSize = Math.min(statements1.size(), statements2.size());
        if (minSize == 0) {
            // If either parent has no statements, return copies of the parents.
            return Pair.of(parent1.copy(), parent2.copy());
        }

        // Select a random crossover point within the range of the shorter parent
        int crossoverPoint = random.nextInt(1, minSize);

        // Create new lists for offspring statements
        List<Statement> offspring1Statements = new ArrayList<>(statements1.subList(0, crossoverPoint));
        offspring1Statements.addAll(statements2.subList(crossoverPoint, statements2.size()));

        List<Statement> offspring2Statements = new ArrayList<>(statements2.subList(0, crossoverPoint));
        offspring2Statements.addAll(statements1.subList(crossoverPoint, statements1.size()));

        // Construct and return new TestChromosome instances
        TestChromosome offspring1 = new TestChromosome(parent1.getMutation(), parent1.getCrossover(), offspring1Statements);
        TestChromosome offspring2 = new TestChromosome(parent2.getMutation(), parent2.getCrossover(), offspring2Statements);

        return Pair.of(offspring1, offspring2);
    }

    /**
     * Provides a string representation of the crossover operator.
     *
     * @return the name of the crossover operator
     */
    @Override
    public String toString() {
        return "TestCrossover (One-Point)";
    }
}
