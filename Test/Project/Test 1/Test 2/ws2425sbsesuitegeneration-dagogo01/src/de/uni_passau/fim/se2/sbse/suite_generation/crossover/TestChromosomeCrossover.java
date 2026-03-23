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
public class TestChromosomeCrossover implements Crossover<TestChromosome> {

    private final Random random;

    /**
     * Constructs a new crossover operator for {@link TestChromosome}.
     */
    public TestChromosomeCrossover() {
        this.random = new Random();
    }

    /**
     * Applies single-point crossover to the two given parent chromosomes.
     *
     * @param parent1 the first parent chromosome
     * @param parent2 the second parent chromosome
     * @return a pair of offspring chromosomes resulting from crossover
     */
    @Override
    public Pair<TestChromosome> apply(final TestChromosome parent1, final TestChromosome parent2) {
        // Log crossover initiation
        System.out.println("[DEBUG] Applying crossover to two parents");

        // Copy parent chromosomes for offspring initialization
        TestChromosome offspring1 = parent1.copy();
        TestChromosome offspring2 = parent2.copy();

        // Retrieve statements from both parents
        List<Statement> parent1Statements = new ArrayList<>(parent1.getStatements());
        List<Statement> parent2Statements = new ArrayList<>(parent2.getStatements());

        if (parent1Statements.isEmpty() || parent2Statements.isEmpty()) {
            System.err.println("[WARNING] One or both parents have no statements");
            return Pair.of(offspring1, offspring2);
        }

        // Determine random crossover points
        int crossoverPoint1 = random.nextInt(parent1Statements.size());
        int crossoverPoint2 = random.nextInt(parent2Statements.size());
        System.out.println("[DEBUG] Crossover points: " + crossoverPoint1 + ", " + crossoverPoint2);

        // Create offspring statements by recombining parent statements
        List<Statement> offspring1Statements = new ArrayList<>();
        List<Statement> offspring2Statements = new ArrayList<>();

        offspring1Statements.addAll(parent1Statements.subList(0, crossoverPoint1));
        offspring1Statements.addAll(parent2Statements.subList(crossoverPoint2, parent2Statements.size()));

        offspring2Statements.addAll(parent2Statements.subList(0, crossoverPoint2));
        offspring2Statements.addAll(parent1Statements.subList(crossoverPoint1, parent1Statements.size()));

        // Replace offspring statements
        offspring1.getStatements().clear();
        offspring1.getStatements().addAll(offspring1Statements);

        offspring2.getStatements().clear();
        offspring2.getStatements().addAll(offspring2Statements);

        // Return offspring as a pair
        return Pair.of(offspring1, offspring2);
    }

    @Override
    public String toString() {
        return "TestChromosomeCrossover (Single-Point)";
    }
}
