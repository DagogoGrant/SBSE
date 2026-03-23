package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestChromosomeMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TestChromosomeCrossoverTest {

    @Mock
    private Mutation<TestChromosome> mutationMock;

    @Mock
    private Crossover<TestChromosome> crossoverMock;

    private TestChromosome parent1;
    private TestChromosome parent2;
    private TestChromosomeCrossover crossover;
    private Random randomMock;

    @BeforeEach
    void setUp() {
        mutationMock = mock(Mutation.class);
        crossoverMock = mock(Crossover.class);
        randomMock = mock(Random.class);
        crossover = new TestChromosomeCrossover(randomMock);

        parent1 = new TestChromosome(mutationMock, crossoverMock, Collections.emptyList());
        parent2 = new TestChromosome(mutationMock, crossoverMock, Collections.emptyList());
        // Mock random generator for predictable results
        randomMock = mock(Random.class);
        
        // Create mock statements
        Statement statement1 = mock(Statement.class);
        Statement statement2 = mock(Statement.class);
        Statement statement3 = mock(Statement.class);
        
        List<Statement> statements1 = List.of(statement1, statement2);
        List<Statement> statements2 = List.of(statement3);

        // Mock test chromosomes
        parent1 = mock(TestChromosome.class);
        parent2 = mock(TestChromosome.class);

        when(parent1.copy()).thenReturn(mock(TestChromosome.class));
        when(parent2.copy()).thenReturn(mock(TestChromosome.class));

        when(parent1.getStatements()).thenReturn(statements1);
        when(parent2.getStatements()).thenReturn(statements2);

        crossover = new TestChromosomeCrossover(randomMock);
    }

    @Test
    void testCrossoverWithNonEmptyParents() {
        when(randomMock.nextInt(anyInt())).thenReturn(1, 0); // Fixed crossover points

        Pair<TestChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertNotNull(offspring.getFst());
        assertNotNull(offspring.getSnd());

        verify(parent1, times(1)).copy();
        verify(parent2, times(1)).copy();
    }

    @Test
    void testCrossoverWithEmptyParents() {
        when(parent1.getStatements()).thenReturn(Collections.emptyList());
        when(parent2.getStatements()).thenReturn(Collections.emptyList());

        Pair<TestChromosome> offspring = crossover.apply(parent1, parent2);

        assertNotNull(offspring);
        assertEquals(0, offspring.getFst().getStatements().size());
        assertEquals(0, offspring.getSnd().getStatements().size());

        verify(parent1, times(1)).copy();
        verify(parent2, times(1)).copy();
    }

   @Test
void testCrossoverWithSingleStatementParents() {
    Statement statementMock = mock(Statement.class);
    List<Statement> singleStatementList = Collections.singletonList(statementMock);

    // Mocking behavior for parents
    when(parent1.copy()).thenReturn(new TestChromosome(mutationMock, crossoverMock, new ArrayList<>(singleStatementList)));
    when(parent2.copy()).thenReturn(new TestChromosome(mutationMock, crossoverMock, new ArrayList<>(singleStatementList)));

    when(parent1.getStatements()).thenReturn(singleStatementList);
    when(parent2.getStatements()).thenReturn(singleStatementList);

    when(randomMock.nextInt(anyInt())).thenReturn(0);

    Pair<TestChromosome> offspring = crossover.apply(parent1, parent2);

    assertNotNull(offspring);
    assertEquals(1, offspring.getFst().getStatements().size(), "First offspring should have 1 statement.");
    assertEquals(1, offspring.getSnd().getStatements().size(), "Second offspring should have 1 statement.");
    assertSame(statementMock, offspring.getFst().getStatements().get(0), "Statement should match the original.");
    assertSame(statementMock, offspring.getSnd().getStatements().get(0), "Statement should match the original.");

    verify(parent1, times(1)).copy();
    verify(parent2, times(1)).copy();
}


    @Test
    void testToStringMethod() {
        assertEquals("TestChromosomeCrossover (Single-Point)", crossover.toString());
    }
}
