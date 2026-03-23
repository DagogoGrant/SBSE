package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseChromosomeGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;

import java.util.*;

public class MutationOperator implements Mutation<TestChromosome> {

    private static final int MAX_STATEMENTS = 50; // Prevent chromosome explosion
    private static final int MAX_RETRIES = 25; // Retry limit for mutations
    private  TestCaseChromosomeGenerator generator;
    int mutationType = weightedRandomMutation();
    


    public MutationOperator(TestCaseChromosomeGenerator generator) {
        this.generator = generator;
    }
    public MutationOperator() {
        this.generator = null; // Initialize to null
    }
    /**
     * Sets the chromosome generator for this mutation operator.
     *
     * @param generator the chromosome generator to set
     */
    public void setGenerator(TestCaseChromosomeGenerator generator) {
        this.generator = generator;
    }

    @Override
    public TestChromosome apply(final TestChromosome parent) {
        TestChromosome offspring = parent.copy();
        List<Statement> statements = offspring.getStatements();

        if (statements.isEmpty()) {
            System.out.println("[DEBUG] No statements to mutate. Returning unchanged chromosome.");
            return offspring;
        }

        boolean mutated = false;
        int retries = MAX_RETRIES;

        while (!mutated && retries-- > 0) {
            int mutationType = Randomness.random().nextInt(4); // 0: Replace, 1: Add, 2: Remove, 3: Swap
            System.out.println("[DEBUG] Attempting mutation type: " + mutationType + " (retries left: " + retries + ")");

            switch (mutationType) {
                case 0 -> mutated = handleReplaceMutation(statements);
                case 1 -> mutated = handleAddMutation(statements);
                case 2 -> mutated = handleRemoveMutation(statements);
                case 3 -> mutated = handleSwapMutation(statements);
            }

            if (mutated) {
                System.out.println("[DEBUG] Mutation succeeded for type: " + mutationType);
            }
        }

        if (!mutated) {
            System.out.println("[ERROR] Mutation failed after " + MAX_RETRIES + " retries. Returning unchanged chromosome.");
        }

        return offspring;
    }

    private boolean handleReplaceMutation(List<Statement> statements) {
        int replaceIndex = Randomness.random().nextInt(statements.size());
        Statement original = statements.get(replaceIndex);
        Statement newStatement = generateUniqueStatement(statements);

        if (newStatement != null && !newStatement.equals(original)) {
            statements.set(replaceIndex, newStatement);
            System.out.println("Replaced statement at index " + replaceIndex + ": " + original + " with " + newStatement);
            return true;
        }

        System.out.println("Replace mutation failed.");
        return false;
    }

    private boolean handleAddMutation(List<Statement> statements) {
        if (statements.size() >= MAX_STATEMENTS) return false;
    
        Statement newStatement = generator.generateRandomStatement();
        if (newStatement != null) {
            int addIndex = Randomness.random().nextInt(statements.size() + 1);
            statements.add(addIndex, newStatement);
            return true;
        }
        return false;
    }
    

    private boolean handleRemoveMutation(List<Statement> statements) {
        if (statements.size() <= 1) {
            System.out.println("Cannot remove statement: Minimum size reached.");
            return false;
        }

        int removeIndex = Randomness.random().nextInt(statements.size());
        Statement removed = statements.remove(removeIndex);
        System.out.println("Removed statement at index " + removeIndex + ": " + removed);
        return true;
    }

    private boolean handleSwapMutation(List<Statement> statements) {
        if (statements.size() < 2) {
            System.out.println("Cannot swap statements: Too few statements.");
            return false;
        }

        int index1 = Randomness.random().nextInt(statements.size());
        int index2 = Randomness.random().nextInt(statements.size());

        if (index1 != index2) {
            Collections.swap(statements, index1, index2);
            System.out.println("Swapped statements at indices " + index1 + " and " + index2);
            return true;
        }

        System.out.println("Swap mutation failed: Indices were the same.");
        return false;
    }

    private Statement generateUniqueStatement(List<Statement> existingStatements) {
        Set<Statement> uniqueStatements = new HashSet<>(existingStatements);

        try {
            for (int i = 0; i < 10; i++) { // Retry up to 10 times
                Statement randomStatement = generateRandomStatement(existingStatements);

                if (randomStatement != null && !uniqueStatements.contains(randomStatement)) {
                    System.out.println("Generated unique statement: " + randomStatement);
                    return randomStatement;
                }
            }
        } catch (Exception e) {
            System.out.println("Error generating unique statement: " + e.getMessage());
        }

        System.out.println("Failed to generate a unique statement after 10 attempts.");
        return null;
    }

    private Statement generateRandomStatement(List<Statement> existingStatements) {
        try {
            TestChromosome tempChromosome = generator.get();
            List<Statement> tempStatements = tempChromosome.getStatements();
    
            if (!tempStatements.isEmpty()) {
                return tempStatements.stream()
                    .filter(stmt -> !existingStatements.contains(stmt))
                    .findAny()
                    .orElse(tempStatements.get(0));
            }
        } catch (Exception e) {
            System.out.println("Error generating random statement: " + e.getMessage());
        }
        return null;
    }
    
    
    private int weightedRandomMutation() {
        int[] weights = {40, 30, 20, 10}; // Example: Replace(40%), Add(30%), Remove(20%), Swap(10%)
        int totalWeight = Arrays.stream(weights).sum();
        int randomValue = Randomness.random().nextInt(totalWeight);
        int cumulativeWeight = 0;
    
        for (int i = 0; i < weights.length; i++) {
            cumulativeWeight += weights[i];
            if (randomValue < cumulativeWeight) {
                return i; // Return mutation type based on weights
            }
        }
        return 0; // Default to Replace
    }
    

    @Override
    public String toString() {
        return "MutationOperator";
    }
}
