package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestChromosomeMutationTest {

    private TestChromosomeMutation mutation;
    private Random mockRandom;
    private List<Statement> availableStatements;
    private TestChromosome parentChromosome;
    private Statement mockStatement;

    @BeforeEach
    void setUp() {
        mockRandom = mock(Random.class);
        availableStatements = new ArrayList<>();
        mockStatement = mock(Statement.class);

        // Add mock statements to the availableStatements list
        availableStatements.add(mockStatement);
        availableStatements.add(mockStatement);

        // Create an instance of TestChromosomeMutation with mock values
        mutation = new TestChromosomeMutation(mockRandom, availableStatements);

        // Mock parent chromosome
        parentChromosome = mock(TestChromosome.class);
        List<Statement> statements = new ArrayList<>();
        statements.add(mockStatement);
        statements.add(mockStatement);

        when(parentChromosome.copy()).thenReturn(parentChromosome);
        when(parentChromosome.getStatements()).thenReturn(statements);
    }

    @Test
    void testConstructorWithValidParameters() {
        assertDoesNotThrow(() -> new TestChromosomeMutation(new Random(), availableStatements));
    }

    @Test
    void testConstructorThrowsExceptionWhenRandomIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new TestChromosomeMutation(null, availableStatements));
        assertEquals("Random and availableStatements must not be null", exception.getMessage());
    }

    @Test
    void testConstructorThrowsExceptionWhenAvailableStatementsIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new TestChromosomeMutation(mockRandom, null));
        assertEquals("Random and availableStatements must not be null", exception.getMessage());
    }

    @Test
    void testApplyMutationReplaceStatement() {
        when(mockRandom.nextInt(3)).thenReturn(0); // Mutation type: Replace
        when(mockRandom.nextInt(2)).thenReturn(1); // Replace at index 1
        when(mockRandom.nextInt(availableStatements.size())).thenReturn(0);

        TestChromosome offspring = mutation.apply(parentChromosome);

        assertNotNull(offspring);
        verify(parentChromosome, times(1)).copy();
    }

    @Test
    void testApplyMutationAddStatement() {
        when(mockRandom.nextInt(3)).thenReturn(1); // Mutation type: Add
        when(mockRandom.nextInt(3)).thenReturn(2); // Add at index 2
        when(mockRandom.nextInt(availableStatements.size())).thenReturn(0);

        TestChromosome offspring = mutation.apply(parentChromosome);

        assertNotNull(offspring);
        verify(parentChromosome, times(1)).copy();
    }

    @Test
    void testApplyMutationRemoveStatement() {
        when(mockRandom.nextInt(3)).thenReturn(2); // Mutation type: Remove
        when(mockRandom.nextInt(2)).thenReturn(0); // Remove at index 0

        TestChromosome offspring = mutation.apply(parentChromosome);

        assertNotNull(offspring);
        verify(parentChromosome, times(1)).copy();
    }

    @Test
    void testApplyMutationDoesNotRemoveLastStatement() {
        List<Statement> singleStatementList = new ArrayList<>();
        singleStatementList.add(mockStatement);
        when(parentChromosome.getStatements()).thenReturn(singleStatementList);
        when(mockRandom.nextInt(3)).thenReturn(2); // Mutation type: Remove

        TestChromosome offspring = mutation.apply(parentChromosome);

        assertEquals(1, offspring.getStatements().size(), "Should not remove the only statement.");
    }

    @Test
    void testApplyMutationWithEmptyStatements() {
        List<Statement> emptyStatements = new ArrayList<>();
        when(parentChromosome.getStatements()).thenReturn(emptyStatements);

        TestChromosome offspring = mutation.apply(parentChromosome);

        assertNotNull(offspring);
        assertTrue(offspring.getStatements().isEmpty(), "Offspring should have no statements.");
    }

    @Test
    void testGenerateRandomStatement() {
        when(mockRandom.nextInt(availableStatements.size())).thenReturn(0);
        TestChromosome offspring = mutation.apply(parentChromosome);
        assertNotNull(offspring);
    }

    @Test
    void testToStringOverride() {
        assertEquals("TestChromosomeMutation", mutation.toString());
    }
    @Test
void testApplyMutationBoundaryConditionsForReplace() {
    when(mockRandom.nextInt(3)).thenReturn(0); // Mutation type: Replace
    when(mockRandom.nextInt(2)).thenReturn(0); // Replace at index 0
    when(mockRandom.nextInt(availableStatements.size())).thenReturn(1); // Select the second statement

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertNotNull(offspring);
    verify(parentChromosome, times(1)).copy();
}

@Test
void testApplyMutationBoundaryConditionsForAdd() {
    when(mockRandom.nextInt(3)).thenReturn(1); // Mutation type: Add
    when(mockRandom.nextInt(3)).thenReturn(0); // Add at index 0
    when(mockRandom.nextInt(availableStatements.size())).thenReturn(1); // Select the second statement

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertNotNull(offspring);
    verify(parentChromosome, times(1)).copy();
}

@Test
void testApplyMutationBoundaryConditionsForRemove() {
    when(mockRandom.nextInt(3)).thenReturn(2); // Mutation type: Remove
    when(mockRandom.nextInt(2)).thenReturn(1); // Remove the last index

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertNotNull(offspring);
    verify(parentChromosome, times(1)).copy();
}

@Test
void testApplyMutationWithZeroStatements() {
    when(parentChromosome.getStatements()).thenReturn(new ArrayList<>());

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertNotNull(offspring);
    assertEquals(0, offspring.getStatements().size());
}

@Test
void testIllegalStateExceptionForInvalidMutationType() {
    when(mockRandom.nextInt(3)).thenReturn(99); // Unexpected mutation type

    Exception exception = assertThrows(IllegalStateException.class, () ->
        mutation.apply(parentChromosome)
    );

    assertEquals("Unexpected mutation type: 99", exception.getMessage());
}

@Test
void testApplyMutationWithMultipleStatements() {
    when(mockRandom.nextInt(3)).thenReturn(1); // Mutation type: Add
    when(mockRandom.nextInt(availableStatements.size())).thenReturn(0);
    when(mockRandom.nextInt(3)).thenReturn(1);  // Insert at position 1

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertNotNull(offspring);
    assertEquals(3, offspring.getStatements().size());
}

// @Test
// void testEmptyAvailableStatementsList() {
//     List<Statement> emptyStatements = new ArrayList<>();
//     mutation = new TestChromosomeMutation(mockRandom, emptyStatements);

//     TestChromosome offspring = mutation.apply(parentChromosome);
//     assertNotNull(offspring);
//     assertEquals(2, offspring.getStatements().size());  // No changes should be made
// }

@Test
void testAddStatementAtEnd() {
    List<Statement> initialStatements = new ArrayList<>();
    initialStatements.add(mockStatement); // Add one statement initially

    when(parentChromosome.copy()).thenReturn(parentChromosome);
    when(parentChromosome.getStatements()).thenReturn(initialStatements);
    when(mockRandom.nextInt(3)).thenReturn(1); // Mutation type: Add
    when(mockRandom.nextInt(2)).thenReturn(1); // Add at end
    when(mockRandom.nextInt(availableStatements.size())).thenReturn(0);

    TestChromosome offspring = mutation.apply(parentChromosome);

    assertEquals(2, offspring.getStatements().size(), "Statement should be added at the end.");
}


}
