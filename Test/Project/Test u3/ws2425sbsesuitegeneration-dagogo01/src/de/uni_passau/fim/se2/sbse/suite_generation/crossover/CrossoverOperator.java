package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;

import java.util.ArrayList;
import java.util.List;

public class CrossoverOperator {

    private static final int MAX_STATEMENTS = 50; // Prevent chromosome explosion

    public List<TestChromosome> apply(TestChromosome parent1, TestChromosome parent2, String crossoverType) {
        switch (crossoverType.toLowerCase()) {
            case "single-point":
                return singlePointCrossover(parent1, parent2);
            case "uniform":
                return uniformCrossover(parent1, parent2);
            default:
                throw new IllegalArgumentException("Unsupported crossover type: " + crossoverType);
        }
    }

    private List<TestChromosome> singlePointCrossover(TestChromosome parent1, TestChromosome parent2) {
        List<Statement> p1Statements = parent1.getStatements();
        List<Statement> p2Statements = parent2.getStatements();

        if (p1Statements.isEmpty() || p2Statements.isEmpty()) {
            System.out.println("[ERROR] One of the parents has no statements. Returning unchanged parents.");
            List<TestChromosome> result = new ArrayList<>();
            result.add(parent1.copy());
            result.add(parent2.copy());
            return result;
        }

        int crossoverPoint = Randomness.random().nextInt(Math.min(p1Statements.size(), p2Statements.size()));
        System.out.println("[DEBUG] Single-point crossover at index: " + crossoverPoint);

        List<Statement> offspring1Statements = new ArrayList<>();
        List<Statement> offspring2Statements = new ArrayList<>();

        offspring1Statements.addAll(p1Statements.subList(0, crossoverPoint));
        offspring1Statements.addAll(p2Statements.subList(crossoverPoint, p2Statements.size()));

        offspring2Statements.addAll(p2Statements.subList(0, crossoverPoint));
        offspring2Statements.addAll(p1Statements.subList(crossoverPoint, p1Statements.size()));

        TestChromosome offspring1 = new TestChromosome(offspring1Statements);
        TestChromosome offspring2 = new TestChromosome(offspring2Statements);

        return validateOffspring(offspring1, offspring2);
    }

    private List<TestChromosome> uniformCrossover(TestChromosome parent1, TestChromosome parent2) {
        List<Statement> p1Statements = parent1.getStatements();
        List<Statement> p2Statements = parent2.getStatements();

        if (p1Statements.isEmpty() || p2Statements.isEmpty()) {
            System.out.println("[ERROR] One of the parents has no statements. Returning unchanged parents.");
            List<TestChromosome> result = new ArrayList<>();
            result.add(parent1.copy());
            result.add(parent2.copy());
            return result;
        }

        List<Statement> offspring1Statements = new ArrayList<>();
        List<Statement> offspring2Statements = new ArrayList<>();

        int maxSize = Math.min(p1Statements.size(), p2Statements.size());

        for (int i = 0; i < maxSize; i++) {
            if (Randomness.random().nextBoolean()) {
                offspring1Statements.add(p1Statements.get(i));
                offspring2Statements.add(p2Statements.get(i));
            } else {
                offspring1Statements.add(p2Statements.get(i));
                offspring2Statements.add(p1Statements.get(i));
            }
        }

        TestChromosome offspring1 = new TestChromosome(offspring1Statements);
        TestChromosome offspring2 = new TestChromosome(offspring2Statements);

        return validateOffspring(offspring1, offspring2);
    }

    private List<TestChromosome> validateOffspring(TestChromosome offspring1, TestChromosome offspring2) {
        List<TestChromosome> validatedOffspring = new ArrayList<>();

        if (offspring1.getStatements().size() <= MAX_STATEMENTS) {
            validatedOffspring.add(offspring1);
        } else {
            System.out.println("[ERROR] Offspring1 exceeds maximum statements. Skipping.");
        }

        if (offspring2.getStatements().size() <= MAX_STATEMENTS) {
            validatedOffspring.add(offspring2);
        } else {
            System.out.println("[ERROR] Offspring2 exceeds maximum statements. Skipping.");
        }

        return validatedOffspring;
    }
}
