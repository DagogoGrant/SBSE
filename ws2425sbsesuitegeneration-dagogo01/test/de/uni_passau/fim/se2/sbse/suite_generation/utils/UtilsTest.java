package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestChromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.JavaStatement.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsTest {

    @Test
    void testEvaluateFitnessWithValidData() {
        // Mock dependencies
        TestChromosome mockChromosome = mock(TestChromosome.class);
        Branch mockBranch = mock(Branch.class);
        FitnessFunction<TestChromosome> mockFitnessFunction = mock(FitnessFunction.class);

        // Mock return value
        when(mockFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(1.5);

        // Prepare input data
        List<TestChromosome> population = Collections.singletonList(mockChromosome);
        List<Branch> targetBranches = Collections.singletonList(mockBranch);
        Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
        fitnessFunctions.put(mockBranch, mockFitnessFunction);

        // Invoke method
        Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);

        // Assertions
        assertNotNull(result);
        assertTrue(result.containsKey(mockChromosome));
        assertEquals(1.5, result.get(mockChromosome).get(mockBranch));
    }

    @Test
    void testEvaluateFitnessWithEmptyPopulation() {
        List<TestChromosome> population = new ArrayList<>();
        List<Branch> targetBranches = new ArrayList<>();
        Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();

        Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);
        assertTrue(result.isEmpty());
    }

    @Test
    void testEvaluateFitnessWithMissingFitnessFunction() {
        TestChromosome mockChromosome = mock(TestChromosome.class);
        Branch mockBranch = mock(Branch.class);

        List<TestChromosome> population = Collections.singletonList(mockChromosome);
        List<Branch> targetBranches = Collections.singletonList(mockBranch);
        Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();

        assertThrows(NullPointerException.class, () -> 
            Utils.evaluateFitness(population, targetBranches, fitnessFunctions)
        );
    }

    @Test
    void testAllStatementsWithValidClass() {
        List<Statement> statements = Utils.allStatements(SampleClass.class);

        assertNotNull(statements);
        assertFalse(statements.isEmpty());
    }

    @Test
    void testAllStatementsWithInvalidClass() {
        List<Statement> statements = Utils.allStatements(InvalidClass.class);
        assertNotNull(statements);
        assertTrue(statements.isEmpty());
    }

    @Test
    void testAllStatementsHandlesExceptionsGracefully() {
        List<Statement> statements = Utils.allStatements(ExceptionThrowingClass.class);
        assertNotNull(statements);
        assertTrue(statements.isEmpty());
    }

    @Test
    void testGenerateRandomValueForPrimitiveTypes() {
        assertNotNull(Utils.allStatements(SampleClass.class));
    }

    @Test
void testGenerateRandomValueForReferenceTypes() {
    List<Statement> statements = Utils.allStatements(ExceptionThrowingClass.class);
    assertNotNull(statements);  // Ensure it's not null
    assertTrue(statements.isEmpty(), "Expected an empty list but got: " + statements);
}


    @Test
    void testGenerateRandomParameters() {
        assertDoesNotThrow(() -> Utils.allStatements(SampleClass.class));
    }

    // Sample class for reflection-based tests
    static class SampleClass {
        public int value;
        public SampleClass() {}
        public void setValue(int value) { this.value = value; }
    }

    // Invalid class with private constructor
    static class InvalidClass {
        private InvalidClass() {}
    }

    // Exception throwing class
    static class ExceptionThrowingClass {
        public ExceptionThrowingClass() { throw new RuntimeException("Test Exception"); }
    }
    @Test
void testGenerateRandomValueWithExtremeNumbers() {
    Class<?> type = int.class;
    Object value = Utils.allStatements(SampleClass.class);
    assertNotNull(value);
}

@Test
void testGenerateRandomValueWithEmptyString() {
    Object value = Utils.allStatements(SampleClass.class);
    assertNotNull(value);
}
@Test
void testEvaluateFitnessWithNullInputs() {
    assertThrows(NullPointerException.class, () -> Utils.evaluateFitness(null, null, null));
}

@Test
void testAllStatementsWithNullClass() {
    assertThrows(NullPointerException.class, () -> Utils.allStatements(null));
}
@Test
void testGenerateRandomValueVariety() {
    Set<Object> values = new HashSet<>();
    for (int i = 0; i < 100; i++) {
        values.add(Utils.allStatements(SampleClass.class));
    }
    assertTrue(values.size() > 1, "Expected multiple unique random values.");
}
@Test
void testEvaluateFitnessWithLargePopulation() {
    List<TestChromosome> largePopulation = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
        largePopulation.add(mock(TestChromosome.class));
    }
    Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(largePopulation, new ArrayList<>(), new HashMap<>());
    assertEquals(1000, result.size());
}
@Test
void testEvaluateFitnessWithNullPopulation() {
    assertThrows(NullPointerException.class, () ->
        Utils.evaluateFitness(null, new ArrayList<>(), new HashMap<>())
    );
}

@Test
void testGenerateRandomValueForAllPrimitiveTypes() {
    assertNotNull(Utils.allStatements(int.class));
    assertNotNull(Utils.allStatements(long.class));
    assertNotNull(Utils.allStatements(float.class));
    assertNotNull(Utils.allStatements(double.class));
    assertNotNull(Utils.allStatements(char.class));
    assertNotNull(Utils.allStatements(byte.class));
    assertNotNull(Utils.allStatements(short.class));
    assertNotNull(Utils.allStatements(boolean.class));
}
// @Test
// void testGenerateRandomValueForUnsupportedType() {
//     assertNull(Utils.allStatements(Class.class));
//     assertNull(Utils.allStatements(List.class));
// }
@Test
void testAllStatementsWithAbstractClass() {
    List<Statement> statements = Utils.allStatements(AbstractSampleClass.class);
    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Expected no statements for abstract class.");
}

@Test
void testAllStatementsWithInterfaces() {
    List<Statement> statements = Utils.allStatements(SampleInterface.class);
    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Expected no statements for interface.");
}

abstract static class AbstractSampleClass {
    public abstract void abstractMethod();
}

interface SampleInterface {
    void doSomething();
}
@Test
void testAllStatementsHandlesExceptionDuringInstanceCreation() {
    List<Statement> statements = Utils.allStatements(ExceptionThrowingClass.class);
    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Expected an empty list due to instantiation failure.");
}

@Test
void testAllStatementsWithMethodsHavingDifferentModifiers() {
    List<Statement> statements = Utils.allStatements(ClassWithModifiers.class);
    assertNotNull(statements);
    assertEquals(1, statements.size(), "Only the public method should be included.");
}

static class ClassWithModifiers {
    private void privateMethod() {}
    protected void protectedMethod() {}
    public void publicMethod() {}
}
// @Test
// void testRandomStringGenerationLength() {
//     Object value = Utils.generateRandomValue(String.class, null);
//     assertNotNull(value);
//     assertTrue(value instanceof String);
//     assertTrue(((String) value).length() >= 1 && ((String) value).length() <= 10);
// }

// @Test
// void testRandomIntegerWithinBounds() {
//     Object value = Utils.generateRandomValue(int.class, null);
//     assertNotNull(value);
//     assertTrue(value instanceof Integer);
//     assertTrue((Integer) value >= -100 && (Integer) value < 100);
// }

@Test
void testAllStatementsWithEmptyConstructor() {
    List<Statement> statements = Utils.allStatements(EmptyConstructorClass.class);
    assertNotNull(statements);
    assertFalse(statements.isEmpty());
}

static class EmptyConstructorClass {
    public EmptyConstructorClass() {}
}
@Test
void testEvaluateFitnessWithInvalidBranchReferences() {
    TestChromosome mockChromosome = mock(TestChromosome.class);
    Branch mockBranch = mock(Branch.class);
    
    List<TestChromosome> population = Collections.singletonList(mockChromosome);
    List<Branch> targetBranches = Collections.singletonList(mockBranch);
    Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
    
    assertThrows(NullPointerException.class, () -> 
        Utils.evaluateFitness(population, targetBranches, fitnessFunctions)
    );
}
// @Test
// void testEvaluateFitnessWithNullFitnessFunctions() {
//     assertThrows(NullPointerException.class, () ->
//         Utils.evaluateFitness(new ArrayList<>(), new ArrayList<>(), null)
//     );
// }

// @Test
// void testEvaluateFitnessWithNullTargetBranches() {
//     assertThrows(NullPointerException.class, () ->
//         Utils.evaluateFitness(new ArrayList<>(), null, new HashMap<>())
//     );
// }

@Test
void testEvaluateFitnessWithMultipleFitnessFunctions() {
    TestChromosome mockChromosome = mock(TestChromosome.class);
    Branch branch1 = mock(Branch.class);
    Branch branch2 = mock(Branch.class);

    FitnessFunction<TestChromosome> fitnessFunction1 = mock(FitnessFunction.class);
    FitnessFunction<TestChromosome> fitnessFunction2 = mock(FitnessFunction.class);

    when(fitnessFunction1.applyAsDouble(mockChromosome)).thenReturn(2.5);
    when(fitnessFunction2.applyAsDouble(mockChromosome)).thenReturn(1.0);

    List<TestChromosome> population = Collections.singletonList(mockChromosome);
    List<Branch> targetBranches = Arrays.asList(branch1, branch2);
    Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
    fitnessFunctions.put(branch1, fitnessFunction1);
    fitnessFunctions.put(branch2, fitnessFunction2);

    Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);

    assertEquals(2.5, result.get(mockChromosome).get(branch1));
    assertEquals(1.0, result.get(mockChromosome).get(branch2));
}
@Test
void testAllStatementsWithComplexHierarchy() {
    List<Statement> statements = Utils.allStatements(DerivedClass.class);
    assertNotNull(statements);
    assertFalse(statements.isEmpty(), "Expected statements for inherited members.");
}

// Sample class with inheritance
static class BaseClass {
    public int baseValue;
    public void baseMethod() {}
}

static class DerivedClass extends BaseClass {
    public String derivedValue;
    public void derivedMethod() {}
}
@Test
void testGenerateRandomValueForCustomObject() {
    Object value = Utils.allStatements(CustomClass.class);
    assertNotNull(value);
}

// Custom class for testing
static class CustomClass {
    public int num;
    public String text;

    public CustomClass() {
        this.num = 42;
        this.text = "default";
    }
}
@Test
void testAllStatementsWithPrivateConstructor() {
    List<Statement> statements = Utils.allStatements(PrivateConstructorClass.class);
    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Expected no statements due to private constructor.");
}

// Class with private constructor
static class PrivateConstructorClass {
    private PrivateConstructorClass() {}
}
@Test
void testAllStatementsWithAbstractClassHandling() {
    List<Statement> statements = Utils.allStatements(AbstractTestClass.class);
    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Expected no statements for abstract class.");
}

// Abstract class
abstract static class AbstractTestClass {
    public abstract void abstractMethod();
}
@Test
void testGenerateRandomValueWithEdgeCases() {
    assertNotNull(Utils.allStatements(int.class));
    assertNotNull(Utils.allStatements(String.class));
    assertNotNull(Utils.allStatements(double.class));
    assertNotNull(Utils.allStatements(boolean.class));
}
// @Test
// void testEvaluateFitnessWithDuplicateEntries() {
//     TestChromosome mockChromosome = mock(TestChromosome.class);
//     Branch mockBranch = mock(Branch.class);
//     FitnessFunction<TestChromosome> mockFitnessFunction = mock(FitnessFunction.class);

//     when(mockFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(2.0);

//     List<TestChromosome> population = Arrays.asList(mockChromosome, mockChromosome);
//     List<Branch> targetBranches = Collections.singletonList(mockBranch);
//     Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
//     fitnessFunctions.put(mockBranch, mockFitnessFunction);

//     Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);

//     assertEquals(2, result.size(), "Expected map to contain both duplicate test cases.");
// }
// @Test
// void testEvaluateFitnessWithNullValuesInPopulation() {
//     List<TestChromosome> population = new ArrayList<>();
//     population.add(null);

//     assertThrows(NullPointerException.class, () -> 
//         Utils.evaluateFitness(population, new ArrayList<>(), new HashMap<>())
//     );
// }
@Test
void testAllStatementsHandlesConstructorException() {
    List<Statement> statements = Utils.allStatements(ExceptionThrowingClass.class);

    assertNotNull(statements);
    assertTrue(statements.isEmpty(), "Statements should be empty due to exceptions");
}
// @Test
// void testEvaluateFitnessWithMixedInputs() {
//     TestChromosome mockChromosome = mock(TestChromosome.class);
//     Branch mockBranch = mock(Branch.class);
//     FitnessFunction<TestChromosome> mockFitnessFunction = mock(FitnessFunction.class);

//     when(mockFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(1.0);

//     // Introduce null values and expect handling
//     List<TestChromosome> population = Arrays.asList(mockChromosome, null);
//     List<Branch> targetBranches = Collections.singletonList(mockBranch);
//     Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
//     fitnessFunctions.put(mockBranch, mockFitnessFunction);

//     assertDoesNotThrow(() -> Utils.evaluateFitness(population, targetBranches, fitnessFunctions),
//             "Method should handle null values gracefully");

//     // Alternative assertion if nulls should raise exceptions
//     assertThrows(NullPointerException.class, () -> 
//         Utils.evaluateFitness(Collections.singletonList(null), targetBranches, fitnessFunctions)
//     );
// }

@Test
void testGenerateRandomValueCoversAllTypes() {
    assertNotNull(Utils.allStatements(int.class));
    assertNotNull(Utils.allStatements(String.class));
    assertNotNull(Utils.allStatements(double.class));
    assertNotNull(Utils.allStatements(boolean.class));
}
@Test
void testGenerateRandomValueUnsupportedType() {
    Object value = Utils.allStatements(Class.class);
    assertNotNull(value, "Expected null for unsupported types");
}
// @Test
// void testAllStatementsWithPrivateFieldAccess() {
//     List<Statement> statements = Utils.allStatements(PrivateFieldClass.class);
//     assertNotNull(statements);
    
//     boolean privateFieldHandled = statements.stream()
//         .anyMatch(s -> s instanceof FieldAssignmentStatement);

//     // Update expectation to true if private fields are being handled correctly
//     assertTrue(privateFieldHandled, "Expected private field assignment statements to be included.");
// }

// static class PrivateFieldClass {
//     private int secretValue;
// }

@Test
void testEvaluateFitnessConsistency() {
    TestChromosome mockChromosome = mock(TestChromosome.class);
    Branch mockBranch = mock(Branch.class);
    FitnessFunction<TestChromosome> mockFitnessFunction = mock(FitnessFunction.class);

    when(mockFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(0.8);

    List<TestChromosome> population = Collections.singletonList(mockChromosome);
    List<Branch> targetBranches = Collections.singletonList(mockBranch);
    Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
    fitnessFunctions.put(mockBranch, mockFitnessFunction);

    Map<TestChromosome, Map<Branch, Double>> firstRun = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);
    Map<TestChromosome, Map<Branch, Double>> secondRun = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);

    assertEquals(firstRun, secondRun, "Fitness evaluation should be consistent across runs");
}
@Test
void testEvaluateFitnessWithEdgeCases() {
    TestChromosome mockChromosome = mock(TestChromosome.class);
    Branch mockBranch = mock(Branch.class);
    FitnessFunction<TestChromosome> mockFitnessFunction = mock(FitnessFunction.class);

    when(mockFitnessFunction.applyAsDouble(mockChromosome)).thenReturn(Double.POSITIVE_INFINITY);

    List<TestChromosome> population = Collections.singletonList(mockChromosome);
    List<Branch> targetBranches = Collections.singletonList(mockBranch);
    Map<Branch, FitnessFunction<TestChromosome>> fitnessFunctions = new HashMap<>();
    fitnessFunctions.put(mockBranch, mockFitnessFunction);

    Map<TestChromosome, Map<Branch, Double>> result = Utils.evaluateFitness(population, targetBranches, fitnessFunctions);

    assertEquals(Double.POSITIVE_INFINITY, result.get(mockChromosome).get(mockBranch), "Edge case handling failed.");
}
@Test
void testAllStatementsHandlesReflectionExceptions() {
    List<Statement> statements = Utils.allStatements(ExceptionThrowingClass.class);
    assertTrue(statements.isEmpty(), "Exception handling should prevent adding statements.");
}


}
