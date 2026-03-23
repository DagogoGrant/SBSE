package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements a single-point crossover strategy for {@link TestChromosome}.
 */
public class TestChromosomeCrossover implements Crossover<TestChromosome>  {

    private final Random random;

    /**
     * Constructs a new crossover operator with a provided {@link Random} instance.
     *
     * @param random the random number generator to use
     */
    public TestChromosomeCrossover(Random random) {
        this.random = random;
    }

    /**
     * Applies single-point crossover to the two given parent chromosomes.
     *
     * @param parent1 the first parent chromosome
     * @param parent2 the second parent chromosome
     * @return a pair of offspring chromosomes resulting from crossover
     */
    @Override
    public Pair<TestChromosome> apply(TestChromosome parent1, TestChromosome parent2) {
        // Create shallow copies of the parents
        TestChromosome offspring1 = parent1.copy();
        TestChromosome offspring2 = parent2.copy();

        // Get the statements from each parent
        List<Statement> parent1Statements = new ArrayList<>(parent1.getStatements());
        List<Statement> parent2Statements = new ArrayList<>(parent2.getStatements());

        // Ensure both parents have at least one statement
        if (parent1Statements.isEmpty() || parent2Statements.isEmpty()) {
            return Pair.of(offspring1, offspring2);
        }

        // Determine a random crossover point for each parent
        int crossoverPoint1 = random.nextInt(parent1Statements.size());
        int crossoverPoint2 = random.nextInt(parent2Statements.size());

        // Create offspring by combining parts of the parents
        List<Statement> offspring1Statements = new ArrayList<>();
        List<Statement> offspring2Statements = new ArrayList<>();

        // Offspring 1: Parent 1's start + Parent 2's end
        offspring1Statements.addAll(parent1Statements.subList(0, crossoverPoint1));
        offspring1Statements.addAll(parent2Statements.subList(crossoverPoint2, parent2Statements.size()));

        // Offspring 2: Parent 2's start + Parent 1's end
        offspring2Statements.addAll(parent2Statements.subList(0, crossoverPoint2));
        offspring2Statements.addAll(parent1Statements.subList(crossoverPoint1, parent1Statements.size()));

        // Set the new statements in the offspring
        offspring1.getStatements().clear();
        offspring1.getStatements().addAll(offspring1Statements);

        offspring2.getStatements().clear();
        offspring2.getStatements().addAll(offspring2Statements);

        return Pair.of(offspring1, offspring2);
    }

    @Override
    public String toString() {
        return "TestChromosomeCrossover (Single-Point)";
    }
}
