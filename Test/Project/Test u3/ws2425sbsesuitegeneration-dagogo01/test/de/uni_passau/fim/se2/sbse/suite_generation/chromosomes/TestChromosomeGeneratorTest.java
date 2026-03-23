package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.examples.*;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.MutationOperator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestChromosomeGeneratorTest {

    /**
     * Tests generating a chromosome for a simple CUT.
     */
    @Test
    void testGenerateChromosomeForSimpleExample() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(SimpleExample.class, 10);
        TestChromosome chromosome = generator.get();

        assertNotNull(chromosome, "Chromosome should not be null");
        List<Statement> statements = chromosome.getStatements();
        assertFalse(statements.isEmpty(), "Chromosome should have at least one statement");
        assertTrue(statements.size() <= 10, "Chromosome should not exceed the maximum number of statements");
    }

    /**
     * Tests generating a chromosome for a complex CUT with multiple methods and fields.
     */
    @Test
    void testGenerateChromosomeForFeatureClass() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(Feature.class, 15);
        TestChromosome chromosome = generator.get();

        assertNotNull(chromosome, "Chromosome should not be null");
        List<Statement> statements = chromosome.getStatements();
        assertFalse(statements.isEmpty(), "Chromosome should have at least one statement");
        assertTrue(statements.size() <= 15, "Chromosome should not exceed the maximum number of statements");
    }

    /**
     * Tests generating a chromosome for a CUT with no public fields or methods.
     */
    @Test
    void testGenerateChromosomeForDeepBranches() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(DeepBranches.class, 5);
        TestChromosome chromosome = generator.get();

        assertNotNull(chromosome, "Chromosome should not be null");
        List<Statement> statements = chromosome.getStatements();
        assertFalse(statements.isEmpty(), "Chromosome should have at least one statement");
        assertTrue(statements.size() <= 5, "Chromosome should not exceed the maximum number of statements");
    }

    /**
     * Tests generating a chromosome for a CUT with both public and private fields.
     */
    @Test
    void testGenerateChromosomeForStackClass() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(Stack.class, 20);
        TestChromosome chromosome = generator.get();

        assertNotNull(chromosome, "Chromosome should not be null");
        List<Statement> statements = chromosome.getStatements();
        System.out.println("Generated statements: " + statements);
        assertFalse(statements.isEmpty(), "Chromosome should have at least one statement");
        assertTrue(statements.size() <= 20, "Chromosome should not exceed the maximum number of statements");
    }

    /**
     * Ensures that the generator handles invalid CUT gracefully.
     */
    @Test
    void testInvalidCUT() {
        assertThrows(RuntimeException.class, () -> {
            TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(null, 10);
            generator.get();
        }, "Generator should throw an exception for a null CUT");
    }

    /**
     * Ensures that generated chromosomes have valid field and method statements.
     */
    @Test
    void testFieldAndMethodStatements() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(Feature.class, 10);
        TestChromosome chromosome = generator.get();

        List<Statement> statements = chromosome.getStatements();
        assertTrue(statements.stream().anyMatch(statement -> statement.toString().contains(".")),
                "Generated chromosome should contain field or method statements");
    }

    /**
     * Ensures that the generator works with a CUT that has multiple constructors.
     */
    @Test
    void testMultipleConstructors() {
        TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(Feature.class, 10);
        TestChromosome chromosome = generator.get();

        List<Statement> statements = chromosome.getStatements();
        assertNotNull(statements.get(0), "First statement should initialize the CUT");
    }
//     @Test
// void testMutationOperatorWithSimpleExample() {
//     TestCaseChromosomeGenerator generator = new TestCaseChromosomeGenerator(SimpleExample.class, 10);
//     MutationOperator mutationOperator = new MutationOperator(generator);

//     TestChromosome original = generator.get();
//     System.out.println("[DEBUG] Original chromosome: " + original);

//     TestChromosome mutated = mutationOperator.apply(original);
//     System.out.println("[DEBUG] Mutated chromosome: " + mutated);

//     assertNotNull(mutated, "Mutated chromosome should not be null");
//     assertNotEquals(original, mutated, "Mutated chromosome should differ from the original");
// }

}
