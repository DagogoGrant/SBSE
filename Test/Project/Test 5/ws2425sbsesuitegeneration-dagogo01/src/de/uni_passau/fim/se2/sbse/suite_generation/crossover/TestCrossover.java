package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCrossover implements Crossover<TestChromosome> {

    private final Random random = new Random();

    @Override
    public Pair<TestChromosome> apply(final TestChromosome parent1, final TestChromosome parent2) {
        // Ensure both parents are not null
        if (parent1 == null || parent2 == null) {
            throw new IllegalArgumentException("Parents cannot be null.");
        }

        // Create copies of the parents to serve as offspring
        TestChromosome offspring1 = parent1.copy();
        TestChromosome offspring2 = parent2.copy();

        // Perform single-point crossover
        int crossoverPoint1 = random.nextInt(parent1.size());
        int crossoverPoint2 = random.nextInt(parent2.size());

        List<Statement> offspring1Statements = new ArrayList<>();
        List<Statement> offspring2Statements = new ArrayList<>();

        // Combine genetic material
        offspring1Statements.addAll(parent1.getStatements().subList(0, crossoverPoint1));
        offspring1Statements.addAll(parent2.getStatements().subList(crossoverPoint2, parent2.size()));

        offspring2Statements.addAll(parent2.getStatements().subList(0, crossoverPoint2));
        offspring2Statements.addAll(parent1.getStatements().subList(crossoverPoint1, parent1.size()));

        // Update offspring chromosomes
        offspring1.setStatements(offspring1Statements);
        offspring2.setStatements(offspring2Statements);

        return Pair.of(offspring1, offspring2);
    }
}
