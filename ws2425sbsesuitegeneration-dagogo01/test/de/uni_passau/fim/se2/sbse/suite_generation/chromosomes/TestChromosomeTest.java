package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Constructor;

/**
 * Unit tests for the TestChromosome class to ensure 100% coverage.
 */
class TestChromosomeTest {

    private Mutation<TestChromosome> mutationMock;
    private Crossover<TestChromosome> crossoverMock;
    private Statement statementMock;

    @BeforeEach
    void setUp() {
        mutationMock = mock(Mutation.class);
        crossoverMock = mock(Crossover.class);
        statementMock = mock(Statement.class);
        
    }

    @Test
    void testConstructorWithMutationAndCrossover() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        assertNotNull(chromosome);
        assertTrue(chromosome.getStatements().isEmpty());
    }

    @Test
    void testConstructorWithStatements() {
        List<Statement> statements = List.of(statementMock);
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock, statements);

        assertEquals(1, chromosome.getStatements().size());
        assertSame(statementMock, chromosome.getStatements().get(0));
    }

    @Test
    void testCopyConstructor() {
        List<Statement> statements = List.of(statementMock);
        TestChromosome original = new TestChromosome(mutationMock, crossoverMock, statements);
        TestChromosome copy = new TestChromosome(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    void testAddStatement() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        chromosome.addStatement(statementMock);

        assertFalse(chromosome.getStatements().isEmpty());
        assertEquals(1, chromosome.getStatements().size());
        assertSame(statementMock, chromosome.getStatements().get(0));
    }

    // @Test
    // void testCallExecutesStatementsAndReturnsBranchData() {
    //     TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    //     Statement mockStatement = mock(Statement.class);
    //     BranchTracer tracerMock = mock(BranchTracer.class);
    
    //     // Set up tracer to return expected values
    //     when(tracerMock.getDistances()).thenReturn(Map.of(1, 0.5));
        
    //     // Inject tracer mock
    //     setBranchTracerInstance(tracerMock);
    
    //     chromosome.addStatement(mockStatement);
    //     Map<Integer, Double> result = chromosome.call();
    
    //     assertEquals(1, result.size(), "Expected one branch to be covered.");
    //     assertEquals(0.5, result.get(1), "Expected branch coverage distance to match.");
        
    //     verify(tracerMock).clear();
    //     verify(mockStatement).run();
    // }
    

    @Test
    void testEqualsAndHashCode() {
        List<Statement> statements = List.of(statementMock);
        TestChromosome chromosome1 = new TestChromosome(mutationMock, crossoverMock, statements);
        TestChromosome chromosome2 = new TestChromosome(mutationMock, crossoverMock, statements);

        assertEquals(chromosome1, chromosome2);
        assertEquals(chromosome1.hashCode(), chromosome2.hashCode());
    }

    @Test
    void testEqualsWithDifferentStatements() {
        List<Statement> statements1 = List.of(statementMock);
        List<Statement> statements2 = List.of(mock(Statement.class));
        TestChromosome chromosome1 = new TestChromosome(mutationMock, crossoverMock, statements1);
        TestChromosome chromosome2 = new TestChromosome(mutationMock, crossoverMock, statements2);

        assertNotEquals(chromosome1, chromosome2);
    }

    @Test
    void testHashCodeConsistency() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        int initialHash = chromosome.hashCode();
        int subsequentHash = chromosome.hashCode();

        assertEquals(initialHash, subsequentHash);
    }

    @Test
    void testSelfMethodReturnsSameInstance() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        assertSame(chromosome, chromosome.self());
    }

    @Test
    void testSetAndGetCrowdingDistance() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        chromosome.setCrowdingDistance(1.5);

        assertEquals(1.5, chromosome.getCrowdingDistance());
    }

    @Test
    void testSetAndGetDensity() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        chromosome.setDensity(0.8);

        assertEquals(0.8, chromosome.getDensity());
    }

    @Test
    void testEqualsWithSameReference() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        assertTrue(chromosome.equals(chromosome));
    }

    @Test
    void testEqualsWithNull() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        assertFalse(chromosome.equals(null));
    }

    @Test
    void testEqualsWithDifferentType() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        assertFalse(chromosome.equals("String"));
    }

    @Test
    void testCopyCreatesNewInstanceWithSameData() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
        chromosome.addStatement(statementMock);
        chromosome.setCrowdingDistance(2.0);
        chromosome.setDensity(3.0);

        TestChromosome copy = chromosome.copy();

        assertNotSame(chromosome, copy);
        assertEquals(chromosome.getStatements(), copy.getStatements());
        assertEquals(chromosome.getCrowdingDistance(), copy.getCrowdingDistance());
        assertEquals(chromosome.getDensity(), copy.getDensity());
    }

    @Test
    void testCallWithEmptyStatements() {
        TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    
        Map<Integer, Double> result = chromosome.call();
    
        assertTrue(result.isEmpty());
    }
    private void setBranchTracerInstance(BranchTracer mockTracer) {
        try {
            Field instanceField = BranchTracer.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, mockTracer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set BranchTracer instance via reflection", e);
        }
    }
    @Test
void testCopyEnsuresDifferentInstance() {
    TestChromosome original = new TestChromosome(mutationMock, crossoverMock);
    TestChromosome copy = original.copy();

    assertNotSame(original, copy, "Copy should be a different instance.");
    assertEquals(original, copy, "Copy should have the same statements.");
}

@Test
void testEqualityAndHashCode() {
    TestChromosome chromosome1 = new TestChromosome(mutationMock, crossoverMock);
    TestChromosome chromosome2 = new TestChromosome(mutationMock, crossoverMock);

    assertEquals(chromosome1, chromosome2, "Chromosomes with the same statements should be equal.");
    assertEquals(chromosome1.hashCode(), chromosome2.hashCode(), "Hash codes should match.");
}

@Test
void testToStringRepresentation() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.addStatement(mock(Statement.class));

    assertNotNull(chromosome.toString(), "toString should return a valid string.");
}
@Test
void testEmptyChromosomeEquality() {
    TestChromosome chromosome1 = new TestChromosome(mutationMock, crossoverMock);
    TestChromosome chromosome2 = new TestChromosome(mutationMock, crossoverMock);

    assertEquals(chromosome1, chromosome2, "Two empty chromosomes should be equal.");
}
@Test
void testStatementListMutation() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.addStatement(statementMock);

    TestChromosome copy = chromosome.copy();
    copy.addStatement(mock(Statement.class));

    assertNotEquals(chromosome, copy, "Mutation should result in non-equal chromosomes.");
}
@Test
void testCrowdingDistanceBoundaries() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.setCrowdingDistance(Double.MIN_VALUE);
    assertEquals(Double.MIN_VALUE, chromosome.getCrowdingDistance());

    chromosome.setCrowdingDistance(Double.MAX_VALUE);
    assertEquals(Double.MAX_VALUE, chromosome.getCrowdingDistance());
}
@Test
void testSetDensityNegativeValues() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.setDensity(-5.0);

    assertEquals(-5.0, chromosome.getDensity(), "Density should handle negative values correctly.");
}
@Test
void testCallMultipleStatements() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    Statement statementMock1 = mock(Statement.class);
    Statement statementMock2 = mock(Statement.class);

    chromosome.addStatement(statementMock1);
    chromosome.addStatement(statementMock2);

    chromosome.call();

    verify(statementMock1, times(1)).run();
    verify(statementMock2, times(1)).run();
}
@Test
void testCopyEnsuresDeepCloning() {
    TestChromosome original = new TestChromosome(mutationMock, crossoverMock);
    original.addStatement(statementMock);
    original.setCrowdingDistance(3.0);
    original.setDensity(4.0);

    TestChromosome copy = original.copy();

    assertNotSame(original.getStatements(), copy.getStatements(), "Copy should have a different statement list instance.");
    assertEquals(original.getCrowdingDistance(), copy.getCrowdingDistance());
    assertEquals(original.getDensity(), copy.getDensity());
}
// @Test
// void testStatementsListImmutability() {
//     List<Statement> initialStatements = List.of(statementMock);
//     TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock, initialStatements);

//     assertThrows(UnsupportedOperationException.class, () -> chromosome.getStatements().add(mock(Statement.class)));
// }
@Test
void testCallWithNullStatements() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.addStatement(null);

    assertThrows(NullPointerException.class, chromosome::call, "Null statement should cause an exception.");
}
// @Test
// void testCallWithEmptyBranchTracer() {
//     TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
//     BranchTracer tracerMock = mock(BranchTracer.class);

//     when(tracerMock.getDistances()).thenReturn(Collections.emptyMap());

//     setBranchTracerInstance(tracerMock);

//     Map<Integer, Double> result = chromosome.call();
//     assertTrue(result.isEmpty(), "Tracer should return an empty map when no branches are covered.");
// }
@Test
void testToStringWithStatements() {
    TestChromosome chromosome = new TestChromosome(mutationMock, crossoverMock);
    chromosome.addStatement(statementMock);

    String representation = chromosome.toString();
    assertNotNull(representation, "toString should provide a valid string representation.");
    assertFalse(representation.isEmpty(), "toString should not return an empty string.");
}


}
