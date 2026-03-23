package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestChromosomeMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.TestChromosomeCrossover;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;


class TestChromosomeGeneratorTest {

    private Mutation<TestChromosome> mutationMock;
    private Crossover<TestChromosome> crossoverMock;
    private Class<?> classUnderTest;
    private TestChromosomeGenerator generator;
    private MockedStatic<Utils> utilsMock;

    @BeforeEach
    void setUp() {
        mutationMock = mock(Mutation.class);
        crossoverMock = mock(Crossover.class);
        classUnderTest = SampleTestClass.class;
        generator = new TestChromosomeGenerator(classUnderTest, mutationMock, crossoverMock);
    }

    @AfterEach
    void tearDown() {
        if (utilsMock != null) {
            utilsMock.close();
        }
    }

    @Test
    void testConstructorWithValidArguments() {
        assertDoesNotThrow(() -> new TestChromosomeGenerator(classUnderTest, mutationMock, crossoverMock));
    }

    @Test
    void testConstructorWithNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new TestChromosomeGenerator(null, mutationMock, crossoverMock));
        assertThrows(IllegalArgumentException.class, () -> new TestChromosomeGenerator(classUnderTest, null, crossoverMock));
        assertThrows(IllegalArgumentException.class, () -> new TestChromosomeGenerator(classUnderTest, mutationMock, null));
    }

    @Test
    void testGetThrowsExceptionWhenNoStatements() {
        // Mock Utils.allStatements to return an empty list
        utilsMock = mockStatic(Utils.class);
        utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(Collections.emptyList());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> generator.get());
        assertEquals("No statements found for class: " + classUnderTest.getName(), exception.getMessage());
    }


    @Test
    void testGetCrossoverReturnsCorrectInstance() {
        Crossover<TestChromosome> crossover = generator.getCrossover();
        assertNotNull(crossover);
        assertTrue(crossover instanceof TestChromosomeCrossover);
    }

    @Test
    void testGetMutationReturnsCorrectInstance() {
        utilsMock = mockStatic(Utils.class);
        utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(Collections.singletonList(mock(Statement.class)));

        Mutation<TestChromosome> mutation = generator.getMutation();
        assertNotNull(mutation);
        assertTrue(mutation instanceof TestChromosomeMutation);
    }

    @Test
    void testRandomSelectionWithSingleStatement() {
        Statement statementMock = mock(Statement.class);
        List<Statement> statementsMock = Collections.singletonList(statementMock);

        utilsMock = mockStatic(Utils.class);
        utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

        TestChromosome chromosome = generator.get();

        assertNotNull(chromosome);
        assertEquals(1, chromosome.getStatements().size());
        assertSame(statementMock, chromosome.getStatements().get(0));
    }
    // @Test
    // void testGetAddsStatementsCorrectly() {
    //     try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
    //         Statement statementMock = mock(Statement.class);
    //         List<Statement> statementsMock = List.of(statementMock, statementMock);
    
    //         mockedUtils.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);
    
    //         Randomness.random().setSeed(42);
    //         TestChromosome chromosome = generator.get();
    
    //         assertEquals(2, chromosome.getStatements().size());
    //     }
    // }
    
    // @Test
    // void testGetGeneratesChromosomeSuccessfully() {
    //     try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
    //         Statement statementMock = mock(Statement.class);
    //         List<Statement> statementsMock = List.of(statementMock, statementMock, statementMock);
    
    //         mockedUtils.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);
    
    //         Randomness.random().setSeed(42);
    //         TestChromosome chromosome = generator.get();
    
    //         assertEquals(3, chromosome.getStatements().size());
    //     }
    // }
    @Test
void testGetAddsConstructorStatementOnly() {
    Statement constructorStatementMock = mock(Statement.class);
    List<Statement> statementsMock = Collections.singletonList(constructorStatementMock);

    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

    TestChromosome chromosome = generator.get();

    assertNotNull(chromosome);
    assertEquals(1, chromosome.getStatements().size());
    assertSame(constructorStatementMock, chromosome.getStatements().get(0));
}

@Test
void testGetWithMultipleStatements() {
    Statement constructorStatementMock = mock(Statement.class);
    Statement fieldAssignmentMock = mock(Statement.class);
    Statement methodInvocationMock = mock(Statement.class);

    List<Statement> statementsMock = List.of(constructorStatementMock, fieldAssignmentMock, methodInvocationMock);

    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

    Randomness.random().setSeed(42);  // Ensure deterministic behavior
    TestChromosome chromosome = generator.get();

    assertNotNull(chromosome);
    assertTrue(chromosome.getStatements().size() >= 1);
}

@Test
void testGetReturnsNonEmptyChromosome() {
    Statement statementMock = mock(Statement.class);
    List<Statement> statementsMock = List.of(statementMock);

    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

    TestChromosome chromosome = generator.get();
    assertNotNull(chromosome);
    assertFalse(chromosome.getStatements().isEmpty());
}

@Test
void testGetHandlesRandomSelectionWithinLimit() {
    Statement statementMock = mock(Statement.class);
    List<Statement> statementsMock = Collections.nCopies(100, statementMock);

    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

    TestChromosome chromosome = generator.get();

    assertNotNull(chromosome);
    assertTrue(chromosome.getStatements().size() <= 50);
}

@Test
void testGetHandlesSmallNumberOfStatements() {
    Statement statementMock = mock(Statement.class);
    List<Statement> statementsMock = List.of(statementMock, statementMock);

    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(statementsMock);

    TestChromosome chromosome = generator.get();

    assertNotNull(chromosome);
    assertTrue(chromosome.getStatements().size() >= 1 && chromosome.getStatements().size() <= 2,
               "Number of statements should be within expected range");
}


@Test
void testGetMutationHandlesEmptyStatements() {
    utilsMock = mockStatic(Utils.class);
    utilsMock.when(() -> Utils.allStatements(classUnderTest)).thenReturn(Collections.emptyList());

    Mutation<TestChromosome> mutation = generator.getMutation();
    assertNotNull(mutation);
}

@Test
void testGetCrossoverCreatesNewInstance() {
    Crossover<TestChromosome> crossover1 = generator.getCrossover();
    Crossover<TestChromosome> crossover2 = generator.getCrossover();

    assertNotSame(crossover1, crossover2);
}

@Test
void testMutationAndCrossoverNotNull() {
    Mutation<TestChromosome> mutation = generator.getMutation();
    Crossover<TestChromosome> crossover = generator.getCrossover();

    assertNotNull(mutation);
    assertNotNull(crossover);
}

    
    /**
     * Sample test class used for reflection-based testing.
     */
    static class SampleTestClass {
        public int value;
        public SampleTestClass() {
            value = 10;
        }

        public void setValue(int newValue) {
            this.value = newValue;
        }
    }
}
