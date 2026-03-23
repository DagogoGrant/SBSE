package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestChromosomeTest {

    private TestChromosome chromosome;
    private Mutation<TestChromosome> mutation;
    private Crossover<TestChromosome> crossover;

    @BeforeEach
    void setUp() {
        mutation = Mockito.mock(Mutation.class);
        crossover = Mockito.mock(Crossover.class);
        chromosome = new TestChromosome(mutation, crossover);
    }

    @Test
    void testAddStatement() {
        Statement statement = Mockito.mock(Statement.class);
        chromosome.addStatement(statement);
        assertEquals(1, chromosome.getStatements().size());
        assertSame(statement, chromosome.getStatements().get(0));
    }

    @Test
    void testCopy() {
        Statement statement1 = Mockito.mock(Statement.class);
        Statement statement2 = Mockito.mock(Statement.class);
        chromosome.addStatement(statement1);
        chromosome.addStatement(statement2);

        TestChromosome copy = chromosome.copy();

        assertNotSame(chromosome, copy);
        assertEquals(2, copy.getStatements().size());
        assertSame(chromosome.getStatements().get(0), copy.getStatements().get(0));
        assertSame(chromosome.getStatements().get(1), copy.getStatements().get(1));
    }

    @Test
    void testEqualsAndHashCode() {
        Statement statement = Mockito.mock(Statement.class);
        chromosome.addStatement(statement);

        TestChromosome identical = new TestChromosome(mutation, crossover);
        identical.addStatement(statement);

        assertEquals(chromosome, identical);
        assertEquals(chromosome.hashCode(), identical.hashCode());

        TestChromosome different = new TestChromosome(mutation, crossover);
        assertNotEquals(chromosome, different);
        assertNotEquals(chromosome.hashCode(), different.hashCode());
    }

    @Test
    void testCallExecutesStatementsAndReturnsBranchDistances() {
        BranchTracer tracer = BranchTracer.getInstance();
        tracer.clear();

        Statement statement1 = Mockito.mock(Statement.class);
        Statement statement2 = Mockito.mock(Statement.class);
        chromosome.addStatement(statement1);
        chromosome.addStatement(statement2);

        // Mock the BranchTracer behavior
        Mockito.doNothing().when(statement1).run();
        Mockito.doNothing().when(statement2).run();

        Map<Integer, Double> distances = chromosome.call();

        assertNotNull(distances);
        Mockito.verify(statement1, Mockito.times(1)).run();
        Mockito.verify(statement2, Mockito.times(1)).run();
    }

    @Test
    void testSelfMethod() {
        assertSame(chromosome, chromosome.self());
    }
}
